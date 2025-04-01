package com.sixthsense.hexastay.service.impl;

import com.sixthsense.hexastay.dto.CommentDTO;
import com.sixthsense.hexastay.dto.CommentViewDTO;
import com.sixthsense.hexastay.entity.Comment;
import com.sixthsense.hexastay.repository.CommRepository;
import com.sixthsense.hexastay.service.CommService;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@ToString
@Log4j2
@Service
@RequiredArgsConstructor
public class CommServiceImpl implements CommService {

    private final ModelMapper modelMapper = new ModelMapper();
    private final CommRepository commRepository;

    @Override
    public Page<CommentViewDTO> getComments(Integer commCategory, Long commContentNum, int page) {

        page = (page < 0) ? 0 : page - 1;

        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());

        Page<Comment> commentPage = commRepository.getAllCommentWithMember(commCategory, commContentNum, pageable);
        return commentPage.map(comm -> modelMapper.map(comm, CommentViewDTO.class));
    }

    @Override
    public void commRegister(CommentDTO commentDTO) {

        Comment comment = modelMapper.map(commentDTO, Comment.class);
        commRepository.save(comment);
    }

    @Override
    public void commDelete(Long commNum){
        commRepository.deleteById(commNum);
    }
}
