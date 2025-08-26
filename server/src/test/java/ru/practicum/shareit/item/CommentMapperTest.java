package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
class CommentMapperTest {
    final CommentMapper commentMapper = new CommentMapper();

    @Test
    public void toComment() {
        NewCommentRequest request = new NewCommentRequest();
        request.setText("Test comment");

        Comment comment = commentMapper.toComment(request);

        assertEquals("Test comment", comment.getText());
    }

    @Test
    public void toCommentDto() {
        User author = new User();
        author.setName("user");

        Comment comment = new Comment();
        comment.setId(100L);
        comment.setText("Test comment");
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        CommentDto dto = commentMapper.toCommentDto(comment);

        assertEquals(100L, dto.getId());
        assertEquals("Test comment", dto.getText());
        assertEquals("user", dto.getAuthorName());
    }

}