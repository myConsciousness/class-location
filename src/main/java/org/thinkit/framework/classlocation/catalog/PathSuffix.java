/*
 * Copyright 2020 Kato Shinya.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.thinkit.framework.classlocation.catalog;

import org.thinkit.common.catalog.Catalog;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * ファイルパスの接尾語を管理するカタログです。
 *
 * @author Kato Shinya
 * @since 1.0
 * @version 1.0
 */
@RequiredArgsConstructor
public enum PathSuffix implements Catalog<PathSuffix> {

    /**
     * 接尾語 : {@code ".class"}
     */
    CLASS(0, ".class");

    /**
     * コード値
     */
    @Getter
    private final int code;

    /**
     * 接尾語
     */
    @Getter
    private final String suffix;

    /**
     * {@link #CLASS} 要素の接尾語を返却します。
     *
     * @return {@link #CLASS} 要素の接尾語
     *
     * @see #CLASS
     */
    public static String clazz() {
        return CLASS.getSuffix();
    }
}