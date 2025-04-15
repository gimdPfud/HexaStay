package com.sixthsense.hexastay.service.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.sixthsense.hexastay.dto.HotelRoomDTO;
import com.sixthsense.hexastay.dto.MemberDTO;
import com.sixthsense.hexastay.entity.Admin;
import com.sixthsense.hexastay.entity.Company;
import com.sixthsense.hexastay.entity.HotelRoom;
import com.sixthsense.hexastay.entity.Member;
import com.sixthsense.hexastay.repository.AdminRepository;
import com.sixthsense.hexastay.repository.CompanyRepository;
import com.sixthsense.hexastay.repository.HotelRoomRepository;
import com.sixthsense.hexastay.repository.MemberRepository;
import com.sixthsense.hexastay.service.HotelRoomService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class HotelRoomServiceImpl implements HotelRoomService {

    //호텔룸 레퍼지토리
    private final HotelRoomRepository hotelRoomRepository;

    //MemberRepository
    private final MemberRepository memberRepository;

    //변환 처리를 위한 ModelMapper
    private final ModelMapper modelMapper = new ModelMapper();

    // 어드민 조회용
    private final AdminRepository adminRepository;
    private final CompanyRepository companyRepository;

    private final QrCodeServiceimpl qrCodeServiceimpl;



    //todo: 메소드 예외 처리는 추후에 할 예정 입니다.

    //******************************//
    //1-1.void 방식의 메서드 체이닝을 이용한 등록 메서드
    @Override
    public HotelRoomDTO insertHotelRoomMember(HotelRoomDTO hotelRoomDTO) {

        // 🟢 hotelRoomDTO 내부에서 memberNum 가져오기
        Long memberNum = hotelRoomDTO.getMemberNum();

        if (memberNum == null) {
            throw new RuntimeException("회원 번호가 누락되었습니다.");
        }

        // 🟢 회원 정보 가져오기
        Member member = memberRepository.findById(memberNum)
                .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));

        // 🟢 DTO → Entity 변환
        HotelRoom hotelRoom = modelMapper.map(hotelRoomDTO, HotelRoom.class);
        hotelRoom.setMember(member);  // 회원 정보 설정

        // 🟢 저장 후 DTO 변환하여 반환
        HotelRoom savedHotelRoom = hotelRoomRepository.save(hotelRoom);
        return modelMapper.map(savedHotelRoom, HotelRoomDTO.class);
    }


    //2-1.호텔룸 리스트 - MemberDTO를 가지고 있는 HotelRoomDTO 서비스 메서드
    @Override
    public Page<HotelRoomDTO> roomMemberPage(Pageable pageable) {
        Page<HotelRoom> hotelRoomPage = hotelRoomRepository.findAll(pageable);

        Page<HotelRoomDTO> hotelRoomDTOPage = hotelRoomPage.map(hotelRoom -> {
            HotelRoomDTO hotelRoomDTO = modelMapper.map(hotelRoom, HotelRoomDTO.class);

            if (hotelRoom.getMember() != null) {
                MemberDTO memberDTO = modelMapper.map(hotelRoom.getMember(), MemberDTO.class);
                hotelRoomDTO.setMemberDTO(memberDTO);
                log.info("매핑된 멤버 정보: {}", memberDTO);
            } else {
                log.info("회원 정보 없음 - 호텔룸 번호: {}", hotelRoom.getHotelRoomNum());
            }

            return hotelRoomDTO;
        });

        return hotelRoomDTOPage;
    }

    //호텔 룸 정보만 가져 오는는 메서드
    /**
     * HotelRoomname만 가져오는 메서드
     */
    @Override
    public HotelRoomDTO HotelRoomByName(String hotelRoomName) {

        HotelRoom hotelRoom = hotelRoomRepository.findByHotelRoomName(hotelRoomName).orElseThrow();
        HotelRoomDTO hotelRoomDTO = modelMapper.map(hotelRoom, HotelRoomDTO.class);
        return hotelRoomDTO;
    }




    //************단일 호텔룸 CRRUD 메소드*************//
    //1.등록 - 이미지 까지 같이 등록 되는 메서드
    @Override
    public void hotelroomInsert(HotelRoomDTO hotelRoomDTO) throws IOException {
        log.info("HotelRoom Service 진입 했습니다. ");

        // 변환 - HotelRoom entity DTO로 변환
        HotelRoom hotelRoom = modelMapper.map(hotelRoomDTO, HotelRoom.class);

        Long companyNum = hotelRoom.getCompany().getCompanyNum();
        Company company = companyRepository.findById(companyNum)
                .orElseThrow(() -> new EntityNotFoundException("회사 정보가 없습니다."));
        hotelRoom.setCompany(company);

        // 처리
        hotelRoomRepository.save(hotelRoom);
        log.info("호텔룸 정보 저장 완료: {}", hotelRoomDTO.getHotelRoomProfile());

        // 들어온 DTO에 사진에 대한 정보가 있다면
        if (hotelRoomDTO.getHotelRoomProfile() != null && !hotelRoomDTO.getHotelRoomProfile().isEmpty()) {
            log.info("이미지 파일 처리 시작: {}", hotelRoomDTO.getHotelRoomProfile());

            // 저장할 때 필요한 데이터들을 설정한다.
            String fileOriginalName = hotelRoomDTO.getHotelRoomProfile().getOriginalFilename();
            String fileFirstName = hotelRoomDTO.getHotelRoomName() + "_" + hotelRoom.getHotelRoomNum();
            String fileSubName = fileOriginalName.substring(fileOriginalName.lastIndexOf("."));
            String fileName = fileFirstName + fileSubName;

            hotelRoomDTO.setHotelRoomProfileMeta("/hotelroom/" + fileName);

            // 파일을 저장할 경로 설정
            Path uploadPath = Paths.get(System.getProperty("user.dir"), "hotelroom/" + fileName);
            Path createPath = Paths.get(System.getProperty("user.dir"), "hotelroom/");
            if (!Files.exists(createPath)) {
                Files.createDirectory(createPath);
            }
            hotelRoomDTO.getHotelRoomProfile().transferTo(uploadPath.toFile());
        }

        // 파일의 데이터(/store/상호명_저장된pk.확장자)를 저장한다.
        hotelRoom.setHotelRoomProfileMeta(hotelRoomDTO.getHotelRoomProfileMeta());

        // QR 코드 경로 생성
        String qrCodePath = "/qr/" + hotelRoomDTO.getHotelRoomNum() + ".png";  // 예: /qr/14.png

        // QR 코드 생성 및 저장
        String qrContent = "/hotel/room/details/" + hotelRoomDTO.getHotelRoomNum();  // QR 코드에 포함할 URL
        qrCodeServiceimpl.generateQrCodeToFile(qrContent, qrCodePath);  // QR 코드 파일 생성

        // QR 코드 경로를 호텔룸 DTO에 설정
        hotelRoomDTO.setHotelRoomQr(qrCodePath);



        // 호텔룸을 저장 (이때, 이미 pk를 가지고 있으므로 update 쿼리가 나간다.)
        hotelRoomRepository.save(hotelRoom);
    }



    //2.리스트
    @Override
    public Page<HotelRoomDTO> hotelroomList(Pageable page) {

        //********페이지 처리 ************//
        //시작 페이지 설정
        int firstPage = page.getPageNumber() - 1;

        //총 토탈 페이지 설정 - 토탈 페이지는 갯수는 여기서 설정 가능
        int pageLimites = 5;

        //페이지 재정의후 페이지 조립
        Pageable pageable =
                PageRequest.of(firstPage, pageLimites,
                        Sort.by(Sort.Direction.DESC,"hotelRoomNum"));

        //*** 변환 및 처리 작업 **//
        //엔티티 변수 선언
        Page<HotelRoom> hotelroomEntity;

        hotelroomEntity =
                hotelRoomRepository.findAll(pageable);

        //todo : memberRepository에서 검색설정후 검색 메서드 구현 예정

        //변환    - 람다식으로 변환
        Page<HotelRoomDTO> hotelRoomDTOS =
                hotelroomEntity.map(data -> modelMapper.map(data, HotelRoomDTO.class));

        //호텔룸 최종 반환 타입
        return hotelRoomDTOS;
    }

    //3.읽기
    @Override
    public HotelRoomDTO hotelroomrRead(Long hotelRoomNum) {

        //Entity 가져오기
        Optional<HotelRoom> optionalHotelRoom =
                hotelRoomRepository.findById(hotelRoomNum);

        //변환 Entity >> DTO
        HotelRoomDTO hotelRoomDTO = modelMapper.map(optionalHotelRoom, HotelRoomDTO.class);


        return hotelRoomDTO;
    }

    //4.수정
    @Override
    public void hotelroomrModify(HotelRoomDTO hotelRoomDTO) {
        try {
            // 존재하는 호텔룸인지 확인 (예외 메시지를 사용자에게 전달할 수 있도록 처리)
            HotelRoom hotelRoom = hotelRoomRepository.findById(hotelRoomDTO.getHotelRoomNum())
                    .orElseThrow(() -> new IllegalArgumentException("해당 호텔룸이 존재하지 않습니다."));

            // DTO → Entity로 덮어쓰기 (기존 엔티티를 기준으로 덮어쓰기)
            modelMapper.map(hotelRoomDTO, hotelRoom);

            // 저장
            hotelRoomRepository.save(hotelRoom);

        } catch (IllegalArgumentException e) {
            // 사용자에게 친절한 메시지 전달을 위한 예외 전파
            throw new IllegalArgumentException(e.getMessage());
        } catch (Exception e) {
            // 시스템 예외 처리
            throw new RuntimeException("호텔룸 정보 수정 중 오류가 발생했습니다.");
        }
    }


        //5.삭제
    @Override
    public void hotelroomDelet(Long hotelRoomNum) {

        //삭제처리 - HotelRoom 있는 pk를 가져와서 행 삭제 처리
        hotelRoomRepository.deleteById(hotelRoomNum);

    }

    //6.호텔룸 조회 - princpal을 활용한 서지스 로직
    @Override
    public List<HotelRoom> listCompany(Long companyNUm) {
        log.info(companyNUm.toString() + "company  num 을 가지고 왔지 ");

        List<HotelRoom> hotelRoomList =
        hotelRoomRepository.findByCompany_CompanyNum(companyNUm);

        return hotelRoomList;
    }





}
