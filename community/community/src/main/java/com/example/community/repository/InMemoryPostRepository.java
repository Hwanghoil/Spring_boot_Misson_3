package com.example.community.repository;


import com.example.community.model.BoardDto;
import com.example.community.model.PostDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

@RequestMapping
public class InMemoryPostRepository implements PostRepository {
    private  final BoardRepository boardRepository;

    private Long lastIndex = 0L;
    private final Map<Long, PostDto> memory = new HashMap<>();

    public  InMemoryPostRepository(
            @Autowired BoardRepository boardRepository){
        this.boardRepository = boardRepository;
    }

    @Override
    public PostDto create(Long boardId, PostDto dto) {
        BoardDto boardDto = this.boardRepository.read(boardId);
        if (boardDto == null){
            return  null;
        }


        dto.setBoardId(boardId);
        lastIndex++;
        dto.setId((lastIndex));
        memory.put(lastIndex, dto);
        memory.get(lastIndex);
        return dto;
    }

    @Override
    public PostDto read(Long boardId, Long postId) {
        PostDto postDto = memory.getOrDefault(postId, null);
        if (postDto == null) {
            return null;
        } else if (!Objects.equals(postDto.getBoardId(),boardId)) {
            return null;

        }
        return postDto;
    }

    @Override
    public Collection<PostDto> readAll(Long boardId) {
        if(boardRepository.read(boardId) == null) return null;
        Collection<PostDto> postList = new ArrayList<>();
        memory.forEach((postId, postDto) -> {
            if(Objects.equals(postDto.getBoardId(),boardId))
                postList.add(postDto);
        });
        return postList;
    }

    @Override
    public boolean update(Long boardId, Long postId, PostDto dto) {
        PostDto targetPost = memory.getOrDefault(postId,null);
        if(targetPost == null){
            return false;
        }
        else if(!Objects.equals(targetPost.getBoardId(), boardId)){
            return false;
        }
        else if(!Objects.equals(targetPost.getPassword(), dto.getPassword())){
            return  false;
        }
        targetPost.setTitle(
                dto.getTitle() == null ? targetPost.getTitle() : dto.getTitle());
        targetPost.setContent(
                dto.getContent() == null ? targetPost.getContent() : dto.getContent());
        return true;
    }

    @Override
    public boolean delete(Long boardId, Long postId, String password) {
        PostDto targetPost = memory.getOrDefault(postId,null);
        if(targetPost == null){
            return false;
        }
        else if(!Objects.equals(targetPost.getBoardId(), boardId)){
            return false;
        }
        else if(!Objects.equals(targetPost.getPassword(), password)){
            return  false;
        }
        memory.remove(postId);
        return true;
    }
}
