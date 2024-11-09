package com.jovisco.pdf.core;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PdfPosY {

    private float y;

    public void incrementBy(float n) {
        y += n;
    }
}
