package com.jovisco.tutorial;

import lombok.Builder;

@Builder
public record ImageDimension(float x, float y, float width, float height) {
}
