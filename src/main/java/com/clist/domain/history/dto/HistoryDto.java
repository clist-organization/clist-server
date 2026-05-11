package com.clist.domain.history.dto;

import com.clist.domain.history.entity.LearningHistory;
import com.clist.domain.history.entity.LearningHistoryList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

public class HistoryDto {

    @Getter
    @NoArgsConstructor
    public static class UpdateRequest {
        private List<ItemRequest> items;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemRequest {
        private String name;
        private String content;
    }

    @Getter
    public static class Response {
        private final UUID id;
        private final List<ItemResponse> items;

        public Response(LearningHistory history, List<LearningHistoryList> items) {
            this.id = history.getId();
            this.items = items.stream().map(ItemResponse::new).toList();
        }
    }

    @Getter
    public static class ItemResponse {
        private final UUID id;
        private final String name;
        private final String content;

        public ItemResponse(LearningHistoryList item) {
            this.id = item.getId();
            this.name = item.getName();
            this.content = item.getContent();
        }
    }
}