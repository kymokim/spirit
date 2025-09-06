package com.kymokim.spirit.link.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class LinkData {
    @Getter
    @Setter
    @Builder
    public static class PathData{
        private PathType type;
        private String id;

        public String getPath() {
            return this.type.getUrl() + "/" + this.id;
        }
    }

    @Getter
    @Setter
    @Builder
    public static class MetaData{
        private String title;
        private String description;
        private String image;
    }
}
