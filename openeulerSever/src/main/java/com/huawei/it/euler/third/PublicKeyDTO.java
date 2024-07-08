/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.third;

import lombok.*;

/**
 * PublicKeyDTO
 *
 * @since 2024/07/04
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicKeyDTO {
    private String algorithm;

    private String publicKey;
}
