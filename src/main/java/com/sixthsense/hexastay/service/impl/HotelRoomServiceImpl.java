package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.dto.HotelRoomDTO;
import com.sixthsense.hexastay.dto.MemberDTO;
import com.sixthsense.hexastay.entity.HotelRoom;
import com.sixthsense.hexastay.entity.Member;
import com.sixthsense.hexastay.repository.HotelRoomRepository;
import com.sixthsense.hexastay.repository.MemberRepository;
import com.sixthsense.hexastay.service.HotelRoomService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
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

    //3-1.
    @Override
    public HotelRoomDTO findRoomWithMembers(Long roomNum) {
        return null;
    }


    //A. optional 타입 - 참조 member가져와서 등록 가능 메서드
    @Override
    public Optional<HotelRoomDTO> getHotelRoomWithMember(Long hotelRoomNum) {
        return hotelRoomRepository.findById(hotelRoomNum)
                .map(hotelRoom -> {
                    HotelRoomDTO hotelRoomDTO = modelMapper.map(hotelRoom, HotelRoomDTO.class);
                    if (hotelRoom.getMember() != null) {
                        hotelRoomDTO.setMemberDTO(modelMapper.map(hotelRoom.getMember(), MemberDTO.class));
                        hotelRoomRepository.save(hotelRoom);    //호텔룸 테이블에 저장
                    }
                    return hotelRoomDTO;
                });
    }




    //B.member 호텔룸 등록 (회원 정보 포함) - Return 타입의 등록 메서드 -Long 타입을 받는 메서드
    @Override
    public HotelRoomDTO insertHotelRoomMember(HotelRoomDTO hotelRoomDTO, Long memberNum) {

        //member 정보를 가져 오기
        Member member = memberRepository.findById(memberNum)
                .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));

        //룸 DB에 member 테이블 합쳐 주기

        HotelRoom hotelRoom = modelMapper.map(hotelRoomDTO, HotelRoom.class);
        hotelRoom.setMember(member); // 회원 정보 설정

        //DB에 저장 설정
        HotelRoom savedHotelRoom = hotelRoomRepository.save(hotelRoom);

        //HotelRoom DTO 에 반환 하기
        return modelMapper.map(savedHotelRoom, HotelRoomDTO.class);
    }



    // C.Member의 정보를 가지고 있는 호텔룸 정보 가져 오기 - List타입
    @Override
    public List<MemberDTO> getAllMembersInHotelRooms() {
        return hotelRoomRepository.findAll().stream()
                .map(HotelRoom::getMember)
                .filter(Objects::nonNull)
                .map(member -> modelMapper.map(member, MemberDTO.class))
                .collect(Collectors.toList());
    }






    //************단일 호텔룸 CRRUD 메소드*************//
    //1.등록
    @Override
    public void hotelroomInsert(HotelRoomDTO hotelRoomDTO) {

        //변환 - Memem만 DTO 타입으로 변환
        HotelRoom hotelRoom = modelMapper.map(hotelRoomDTO, HotelRoom.class);

        //처리
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

        //변환 - Memem만 DTO 타입으로 변환
        HotelRoom hotelRoom = modelMapper.map(hotelRoomDTO, HotelRoom.class);

        //처리
        hotelRoomRepository.save(hotelRoom);

    }

    //5.삭제
    @Override
    public void hotelroomDelet(Long hotelRoomNum) {

        //삭제처리 - HotelRoom 있는 pk를 가져와서 행 삭제 처리
        hotelRoomRepository.deleteById(hotelRoomNum);

    }



}
