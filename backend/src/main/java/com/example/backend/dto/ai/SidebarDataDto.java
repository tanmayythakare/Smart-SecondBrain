package com.example.backend.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SidebarDataDto {
    private List<UpcomingTask> upcomingTasks;
    private String memoryInsight;
    private RelatedNote relatedNote;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpcomingTask {
        private String title;
        private String time;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RelatedNote {
        private String title;
        private String lastEdited;
    }
}
