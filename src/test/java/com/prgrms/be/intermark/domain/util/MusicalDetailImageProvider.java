package com.prgrms.be.intermark.domain.util;

import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.musical.model.MusicalDetailImage;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MusicalDetailImageProvider {

    public static MusicalDetailImage createMusicalDetailImage(Musical musical) {
        return MusicalDetailImage.builder()
                .originalFileName("업로드 파일1")
                .imageUrl("a")
                .musical(musical)
                .build();
    }

}
