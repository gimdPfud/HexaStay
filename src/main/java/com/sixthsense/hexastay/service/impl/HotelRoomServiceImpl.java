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
import org.springframework.data.domain.*;
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
import java.time.LocalDateTime;
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
    // 1. 호텔방 등록 (이미지 + QR 코드까지 함께 등록하는 메서드)
    @Override
    public void hotelroomInsert(HotelRoomDTO hotelRoomDTO) throws IOException {
        log.info("HotelRoom Service 진입 했습니다."); // 이 메서드가 실행되었다는 로그 출력

        // DTO → Entity로 바꿔주는 코드 (HotelRoomDTO → HotelRoom)
        HotelRoom hotelRoom = modelMapper.map(hotelRoomDTO, HotelRoom.class);

        // DTO에 있는 회사 번호(companyNum)를 꺼내서, DB에서 실제 회사 정보를 찾아오기
        Long companyNum = hotelRoom.getCompany().getCompanyNum();
        Company company = companyRepository.findById(companyNum)
                .orElseThrow(() -> new EntityNotFoundException("회사 정보가 없습니다.")); // 회사 없으면 오류
        hotelRoom.setCompany(company); // 찾은 회사 정보를 호텔방에 다시 넣어줌

        // 일단 호텔방 정보를 DB에 저장 (PK 값 생김)
        hotelRoomRepository.save(hotelRoom);
        log.info("호텔룸 정보 저장 완료: {}", hotelRoomDTO.getHotelRoomProfile());

        // 만약 이미지 파일이 있다면, 저장 처리를 시작함
        if (hotelRoomDTO.getHotelRoomProfile() != null && !hotelRoomDTO.getHotelRoomProfile().isEmpty()) {
            log.info("이미지 파일 처리 시작: {}", hotelRoomDTO.getHotelRoomProfile());

            // 이미지 파일 이름 설정 (ex. 방이름_번호.png)
            String fileOriginalName = hotelRoomDTO.getHotelRoomProfile().getOriginalFilename();
            String fileFirstName = hotelRoomDTO.getHotelRoomName() + "_" + hotelRoom.getHotelRoomNum();
            String fileSubName = fileOriginalName.substring(fileOriginalName.lastIndexOf("."));
            String fileName = fileFirstName + fileSubName;

            // 나중에 이미지 보여줄 때 쓸 경로 저장
            hotelRoomDTO.setHotelRoomProfileMeta("/hotelroom/" + fileName);

            // 이미지 파일을 저장할 경로 지정
            Path uploadPath = Paths.get(System.getProperty("user.dir"), "hotelroom/" + fileName);
            Path createPath = Paths.get(System.getProperty("user.dir"), "hotelroom/");
            if (!Files.exists(createPath)) {
                Files.createDirectory(createPath); // 폴더 없으면 새로 만들기
            }

            // 이미지 파일을 컴퓨터에 저장
            hotelRoomDTO.getHotelRoomProfile().transferTo(uploadPath.toFile());
        }

        // 이미지 경로를 실제 Entity에도 넣어주기
        hotelRoom.setHotelRoomProfileMeta(hotelRoomDTO.getHotelRoomProfileMeta());

        try {
            // ✅ QR 코드 만들기 시작!
            // QR 코드에 넣을 URL 주소 (핸드폰으로 찍으면 이 주소로 이동함)
            String qrText = "https://f66c-116-33-138-85.ngrok-free.app/roomlist/roompassword"; // 👉 여기 나중에 진짜 URL로 바꾸면 됨!

            // QR 코드 이미지 파일 이름 만들기 (ex. 방이름_qr.png)
            String fileName = hotelRoom.getHotelRoomName() + "_qr.png";
            Path uploadPath = Paths.get(System.getProperty("user.dir"), "qr/" + fileName);
            Path createPath = Paths.get(System.getProperty("user.dir"), "qr/");
            if (!Files.exists(createPath)) {
                Files.createDirectory(createPath); // 폴더 없으면 만들기
            }

            log.info(fileName + " 파일 네임 경로 까지는 들어 와 지니 ??????");

            // QR 코드 만들기 도구로 이미지 생성
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrText, BarcodeFormat.QR_CODE, 300, 300); // QR 크기 300x300
            Path path = uploadPath;
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path); // QR 코드를 파일로 저장

            log.info(path + "파일 생성은 되고 있는 거냐 있냐고 ~~~~~~~");

            // QR 이미지 파일 경로를 DB에 저장할 수 있게 세팅
            hotelRoom.setHotelRoomQr(fileName);

            // QR 이미지까지 포함된 호텔방 정보 다시 저장
            hotelRoomRepository.save(hotelRoom);

        } catch (Exception e) {
            // QR 코드 만들다가 에러나면 알려주기
            throw new RuntimeException("호텔 룸 등록 중 오류 발생: " + e.getMessage());
        }

        // 다시 한번 전체 정보 저장 (이미 PK가 있어서 update처럼 동작함)
        hotelRoomRepository.save(hotelRoom);
    }

    @Override
    public Page<HotelRoomDTO> searchHotelRoomsByName(String keyword, Pageable pageable) {
        List<HotelRoom> filteredList = hotelRoomRepository.searchByName(keyword);

        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();
        int start = Math.min(page * size, filteredList.size());
        int end = Math.min(start + size, filteredList.size());

        List<HotelRoomDTO> pagedList = filteredList.subList(start, end).stream()
                .map(room -> modelMapper.map(room, HotelRoomDTO.class))
                .collect(Collectors.toList());

        return new PageImpl<>(pagedList, pageable, filteredList.size());
    }




    //todo:http://localhost:8090/register-hotelroom
    //2.리스트
    @Override
    public Page<HotelRoomDTO> hotelroomList(Pageable page) {
        // 페이지 번호 0 이상 유지
        int firstPage = Math.max(page.getPageNumber(), 0);

        // 페이지 크기 제한 (예: 50개 이상은 제한)
        int pageSize = (page.getPageSize() > 0 && page.getPageSize() <= 50) ? page.getPageSize() : 30;

        Pageable pageable = PageRequest.of(firstPage, pageSize, Sort.by(Sort.Direction.DESC, "hotelRoomNum"));

        Page<HotelRoom> hotelroomEntity = hotelRoomRepository.findAll(pageable);

        // DTO로 변환
        return hotelroomEntity.map(entity -> modelMapper.map(entity, HotelRoomDTO.class));
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

    public void hotelroomUpdate(Long hotelRoomNum, HotelRoomDTO hotelRoomDTO) throws IOException {
        log.info("HotelRoom 수정 Service 진입");

        // 1. 기존 HotelRoom 조회
        HotelRoom hotelRoom = hotelRoomRepository.findById(hotelRoomNum)
                .orElseThrow(() -> new EntityNotFoundException("해당 호텔룸이 존재하지 않습니다."));

        // 2. 수동 필드 매핑
        hotelRoom.setHotelRoomName(hotelRoomDTO.getHotelRoomName()); //이름
        hotelRoom.setHotelRoomType(hotelRoomDTO.getHotelRoomType()); //룸 종류(싱글 / 더블 / 스위트)
        hotelRoom.setHotelRoomContent(hotelRoomDTO.getHotelRoomContent());   //룸 정보
        hotelRoom.setHotelRoomPrice(hotelRoomDTO.getHotelRoomPrice());   //룸 가격
        hotelRoom.setHotelRoomPhone(hotelRoomDTO.getHotelRoomPhone());   //룸 전화번호
        hotelRoom.setHotelRoomStatus(hotelRoomDTO.getHotelRoomStatus()); //룸 상태
        hotelRoom.setHotelRoomLodgment(hotelRoomDTO.getHotelRoomLodgment()); //룸 숙박일
        hotelRoom.setHotelRoomPassword(hotelRoomDTO.getHotelRoomPassword()); //룸 패스워드

        hotelRoom.setHotelRoomQr(hotelRoomDTO.getHotelRoomQr()); //룸 QR 코드
        hotelRoom.setHotelRoomProfileMeta(hotelRoomDTO.getHotelRoomProfileMeta()); //이미지 경로 저장

        hotelRoom.setModifyDate(LocalDateTime.now());

        // 3. Member 연관관계 설정 (nullable)
        if (hotelRoomDTO.getMemberNum() != null) {
            Member member = memberRepository.findById(hotelRoomDTO.getMemberNum())
                    .orElseThrow(() -> new IllegalArgumentException("해당 멤버가 존재하지 않습니다."));
            hotelRoom.setMember(member);
        } else {
            hotelRoom.setMember(null); // 제거하고 싶은 경우
        }

        // 4. Company 연관관계 유지
        Company company = companyRepository.findById(hotelRoom.getCompany().getCompanyNum())
                .orElseThrow(() -> new EntityNotFoundException("회사 정보가 없습니다."));
        hotelRoom.setCompany(company);


        // 5. 프로필 이미지 수정
        if (hotelRoomDTO.getHotelRoomProfile() != null) {

        if (hotelRoomRepository.findById(hotelRoomNum).get().getHotelRoomProfileMeta() != null){
            String orifile = hotelRoomRepository.findById(hotelRoomNum).get().getHotelRoomProfileMeta();

            Path deletePath = Paths.get(System.getProperty("user.dir"), orifile.startsWith("/") ? orifile.substring(1) : orifile);

            try {
                Files.deleteIfExists(deletePath);
                log.info("기존 파일 삭제 완료: {}", deletePath);
            } catch (IOException e) {
                log.warn("기존 파일 삭제 실패: {}", e.getMessage());
            }
        }

            String ext = hotelRoomDTO.getHotelRoomProfile().getOriginalFilename()
                    .substring(hotelRoomDTO.getHotelRoomProfile().getOriginalFilename().lastIndexOf("."));
            String fileName = hotelRoomDTO.getHotelRoomName() + "_" + hotelRoomNum + ext;

            Path savePath = Paths.get(System.getProperty("user.dir"), "hotelroom", fileName);


            Files.createDirectories(savePath.getParent());

            hotelRoomDTO.getHotelRoomProfile().transferTo(savePath.toFile());
            hotelRoomDTO.setHotelRoomProfileMeta("/hotelroom/" + fileName);

            // 이 부분 추가!!
            hotelRoom.setHotelRoomProfileMeta(hotelRoomDTO.getHotelRoomProfileMeta());

        } else if (hotelRoomDTO.getHotelRoomProfile() == null) {
            hotelRoomDTO.setHotelRoomProfileMeta(hotelRoom.getHotelRoomProfileMeta());
        }

        // 6. QR코드 삭제 후 재생성
        if (hotelRoom.getHotelRoomQr() != null) {
            // 기존 QR 코드 삭제
            Path qrPath = Paths.get(System.getProperty("user.dir"), "qr", hotelRoom.getHotelRoomQr());
            try {
                Files.deleteIfExists(qrPath);
            } catch (IOException e) {
                log.warn("QR 코드 삭제 실패: {}", e.getMessage());
            }
        }

        // QR 코드 생성 및 경로 설정
        String qrText = "localhost:8090/qr/" + hotelRoomDTO.getHotelRoomNum();
        String qrFileName = hotelRoom.getHotelRoomName() + "_qr.png"; // QR 파일 이름
        Path qrPath = Paths.get(System.getProperty("user.dir"), "qr", qrFileName); // 파일 경로
        Files.createDirectories(qrPath.getParent()); // 경로 생성

        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrText, BarcodeFormat.QR_CODE, 300, 300);
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", qrPath);

            // QR 코드 파일 경로를 hotelRoom 객체에 저장 (view에서 사용할 수 있도록)
            hotelRoom.setHotelRoomQr("/qr/" + qrFileName); // 경로를 "/qr/{파일명}" 형식으로 설정

        } catch (Exception e) {
            throw new RuntimeException("QR 코드 생성 중 오류: " + e.getMessage());
        }

        // 7. 저장
        hotelRoomRepository.save(hotelRoom);
        log.info("호텔룸 정보 수정 완료: {}", hotelRoom.getHotelRoomNum());
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



    // 정산전용
    @Override
    public List<HotelRoomDTO> getSettleList(Long companyNum) {
        List<HotelRoom> hotelRoomList = hotelRoomRepository.findByCompany_CompanyNum(companyNum);
        List<HotelRoomDTO> hotelRoomDTOList = new ArrayList<>();
        for (HotelRoom hotelRoom : hotelRoomList) {
            HotelRoomDTO hotelRoomDTO = new HotelRoomDTO();
            hotelRoomDTO.setHotelRoomNum(hotelRoom.getHotelRoomNum());
            hotelRoomDTO.setHotelRoomName(hotelRoom.getHotelRoomName());
            hotelRoomDTOList.add(hotelRoomDTO);
        }
        return hotelRoomDTOList;
    }

    @Override
    public Page<HotelRoomDTO> getSettleList(Long companyNum, Pageable pageable) {

        Page<HotelRoom> hotelRoomList = hotelRoomRepository.findByCompany_CompanyNum(companyNum, pageable);
        Page<HotelRoomDTO> hotelRoomDTOList = hotelRoomList.map(data -> modelMapper.map(data, HotelRoomDTO.class));
        return hotelRoomDTOList;
    }


}
