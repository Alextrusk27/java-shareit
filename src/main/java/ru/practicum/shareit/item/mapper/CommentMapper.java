package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.projection.CommentWithItemIdProjection;
import ru.practicum.shareit.item.dto.request.CreateCommentRequest;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {
    @Mapping(source = "author.name", target = "authorName")
    CommentDto toCommentDto(Comment comment);

    @Mapping(target = "item", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "created", ignore = true)
    Comment toCommentFromCreate(CreateCommentRequest createRequest);

    CommentDto projectionToCommentDto(CommentWithItemIdProjection projection);

    default Map<Long, List<CommentDto>> toGroupedCommentsMap(List<CommentWithItemIdProjection> projections) {
        return projections.stream()
                .collect(Collectors.groupingBy(
                        CommentWithItemIdProjection::getItemId,
                        Collectors.mapping(this::projectionToCommentDto, Collectors.toList())
                ));
    }
}
