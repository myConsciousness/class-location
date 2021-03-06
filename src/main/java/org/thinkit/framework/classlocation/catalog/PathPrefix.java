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
 * ファイルパスの接頭語を管理するカタログです。
 *
 * @author Kato Shinya
 * @since 1.0
 * @version 1.0
 */
@RequiredArgsConstructor
public enum PathPrefix implements Catalog<PathPrefix> {

    /**
     * 接頭語 : {@code "jar:"}
     */
    JAR(0, "jar:"),

    /**
     * 接頭語 : {@code "file:/"}
     */
    FILE(1, "file:/");

    /**
     * コード値
     */
    @Getter
    private final int code;

    /**
     * 接頭語
     */
    @Getter
    private final String prefix;

    /**
     * {@link #JAR} 要素の接頭語を返却します。
     *
     * @return {@link #JAR} 要素の接頭語
     *
     * @see #JAR
     */
    public static String jar() {
        return JAR.getPrefix();
    }

    /**
     * {@link #FILE} 要素の接頭語を返却します。
     *
     * @return {@link #FILE} 要素の接頭語
     *
     * @see #FILE
     */
    public static String file() {
        return FILE.getPrefix();
    }
}