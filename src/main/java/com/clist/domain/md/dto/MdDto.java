package com.clist.domain.md.dto;

import com.clist.domain.md.entity.MdDocument;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

public class MdDto {

    @Getter
    @NoArgsConstructor
    public static class CreateRequest {
        private String title;
        private String content;
    }

    @Getter
    public static class Response {
        private final UUID id;
        private final String title;
        private final String content;

        public Response(MdDocument md) {
            this.id = md.getId();
            this.title = md.getTitle();
            this.content = md.getContent();
        }
    }
}