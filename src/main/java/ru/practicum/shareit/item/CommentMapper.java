package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.NewCommentRequest;
import ru.practicum.shareit.item.model.Comment;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {
    public Comment toComment(NewCommentRequest request) {
        Comment comment = new Comment();
        comment.setText(request.getText());

        return comment;
    }

    public CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }
}
