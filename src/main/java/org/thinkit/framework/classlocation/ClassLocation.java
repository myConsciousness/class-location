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

package org.thinkit.framework.classlocation;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.thinkit.common.util.PlatformChecker;
import org.thinkit.framework.classlocation.catalog.PathPrefix;
import org.thinkit.framework.classlocation.catalog.PathSuffix;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

/**
 * クラスファイルが格納されている領域を特定します。
 *
 * <pre>
 * クラスが格納されている領域へのURLを取得する場合:
 * <code>
 * Url classUrl = ClassLocation.of(clazz).toUrl();
 * </code>
 * </pre>
 *
 * <pre>
 * クラスファイルに紐づくパッケージのルートまで {@link File} オブジェクトを取得する場合:
 * <code>
 * File classFile = ClassLocation.of(clazz).toFile();
 * </code>
 * </pre>
 *
 * @author Kato Shinya
 * @since 1.0
 * @version 1.0
 */
@ToString
@EqualsAndHashCode
public final class ClassLocation {

    /**
     * ファイルパスのregexパターン
     */
    private static final String FILE_PATH_PATTERN = PathPrefix.file() + "[A-Za-z]:.*";

    /**
     * 検索対象クラス
     */
    private Class<?> clazz;

    /**
     * デフォルトコンストラクタ
     */
    private ClassLocation() {
    }

    /**
     * コンストラクタ
     *
     * @param clazz 検索対象クラス
     *
     * @exception NullPointerException 引数として {@code null} が渡された場合
     */
    private ClassLocation(@NonNull final Class<?> clazz) {
        this.clazz = clazz;
    }

    /**
     * 引数として渡された {@code clazz} を基に {@link ClassLocation} クラスの新しいインスタンスを生成し返却します。
     *
     * @param clazz 検索対象のクラス
     * @return {@link ClassLocation} クラスの新しいインスタンス
     *
     * @exception NullPointerException 引数として {@code null} が渡された場合
     */
    public static ClassLocation of(@NonNull final Class<?> clazz) {
        return new ClassLocation(clazz);
    }

    /**
     * {@link ClassLocation} クラスのインスタンス生成時に渡されたクラスの基準ファイルパスを取得し返却します。
     * <p>
     * {@link ClassLocation}
     * クラスのインスタンス生成時に渡されたクラスのファイルがある特定のjarファイルの中に格納されている場合は接尾語が {@code ".jar"}
     * までのファイルパスを返却します。つまり、パッケージのルートまでを返却します。
     *
     * <pre>
     * 例:
     * クラスファイルが以下のような構成の場所に格納されていると想定します。
     * >> <code>"/path/to/hoge-hoge.jar!/root/package/Hoge.class"</code>
     *
     * 上記の場合は以下のような結果を得られます。
     * >> <code>"file:/path/to/hoge-hoge.jar"</code>
     * </pre>
     *
     * <p>
     * {@link ClassLocation}
     * クラスのインスタンス生成時に渡されたクラスのファイルがファイルシステムに格納されている場合はパッケージのルートまでのファイルパスを返却します。
     * この時に返却される文字列にファイル名は含まれません。
     *
     * <pre>
     * 例:
     * クラスファイルが以下のような構成の場所に格納されていると想定します。
     * >> <code>"/path/to/root/package/Hoge.class"</code>
     *
     * 上記の場合は以下のような結果を得られます。
     * >> <code>"file:/path/to"</code>
     * </pre>
     *
     * @return {@link ClassLocation} クラスのインスタンス生成時に渡されたクラスの基準ファイルパス
     *
     * @throws InvalidClassLocationException クラス名に紐づくクラスリソースが存在しない場合、
     *                                       または、クラスから取得したURLの接尾語が不正な場合、
     *                                       または、クラスから取得したURLを {@link URL}
     *                                       オブジェクトへ変換する処理が失敗した場合
     */
    public URL toUrl() {

        final URL codeSourceLocation = this.clazz.getProtectionDomain().getCodeSource().getLocation();

        if (codeSourceLocation != null) {
            return codeSourceLocation;
        }

        final String className = this.clazz.getSimpleName() + PathSuffix.clazz();
        final URL classResource = this.clazz.getResource(className);

        if (classResource == null) {
            throw new InvalidClassLocationException(String.format("%s not found", className));
        }

        final String url = classResource.toString();
        final String suffix = this.clazz.getCanonicalName().replace('.', '/') + PathSuffix.clazz();

        if (!url.endsWith(suffix)) {
            throw new InvalidClassLocationException(String.format("Invalid suffix was detected in %s", url));
        }

        try {
            return new URL(this.removeJarPrefix(url, suffix));
        } catch (MalformedURLException e) {
            throw new InvalidClassLocationException(e);
        }
    }

    /**
     * {@link ClassLocation} クラスのインスタンス生成時に渡されたクラスのファイルパスを {@link File}
     * オブジェクトへ変換し返却します。
     * <p>
     * {@link URL} オブジェクトへの変換は {@link #toUrl()} メソッドを使用しています。
     *
     * @return {@link ClassLocation} クラスのインスタンス生成時に渡されたクラスのファイルパスを変換した {@link File}
     *         オブジェクト
     *
     * @throws InvalidClassLocationException 不正なファイルパスを検知した場合
     */
    public File toFile() {

        final URL url = this.toUrl();
        String path = url.toString();

        if (path.startsWith(PathPrefix.jar())) {
            path = path.substring(4, path.indexOf("!/"));
        }

        if (PlatformChecker.isWindows() && path.matches(FILE_PATH_PATTERN)) {
            path = PathPrefix.file() + path.substring(5);
        }

        try {
            return new File(new URL(path).toURI());
        } catch (MalformedURLException | URISyntaxException ignore) {
            if (path.startsWith(PathPrefix.file())) {
                return new File(path.substring(5));
            } else {
                throw new InvalidClassLocationException(String.format("Invalid URL -> %s", url.toString()));
            }
        }
    }

    /**
     * 接頭語の {@link PathPrefix#jar()} を取り除いたパスを返却します。引数として指定された {@code suffix}
     * までの文字列も取り除きます。
     *
     * @param url    クラスが格納されている領域へのURL
     * @param suffix 削除する接尾語
     * @return 接頭語 {@link PathPrefix#jar()} と引数として渡された {@code suffix} を取り除いたファイルパス
     */
    private String removeJarPrefix(final String url, final String suffix) {

        final String path = url.substring(0, url.length() - suffix.length());

        if (path.startsWith(PathPrefix.jar())) {
            return path.substring(4, path.length() - 2);
        }

        return path;
    }
}