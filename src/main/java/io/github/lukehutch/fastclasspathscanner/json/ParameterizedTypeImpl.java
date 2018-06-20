/*
 * This file is part of FastClasspathScanner.
 *
 * Author: Luke Hutchison
 *
 * Hosted at: https://github.com/lukehutch/fast-classpath-scanner
 *
 * --
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 Luke Hutchison
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without
 * limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN
 * AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.github.lukehutch.fastclasspathscanner.json;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;

/** An implementation of {@link ParameterizedType}, used to replace type variables with concrete types. */
class ParameterizedTypeImpl implements ParameterizedType {
    private final Type[] actualTypeArguments;
    private final Class<?> rawType;
    private final Type ownerType;

    ParameterizedTypeImpl(final Class<?> rawType, final Type[] actualTypeArguments, final Type ownerType) {
        this.actualTypeArguments = actualTypeArguments;
        this.rawType = rawType;
        this.ownerType = (ownerType != null) ? ownerType : rawType.getDeclaringClass();
        if (rawType.getTypeParameters().length != actualTypeArguments.length) {
            throw new IllegalArgumentException("Argument length mismatch");
        }
    }

    @Override
    public Type[] getActualTypeArguments() {
        return actualTypeArguments.clone();
    }

    @Override
    public Class<?> getRawType() {
        return rawType;
    }

    @Override
    public Type getOwnerType() {
        return ownerType;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ParameterizedType)) {
            return false;
        }
        final ParameterizedType other = (ParameterizedType) o;

        final Type otherOwnerType = other.getOwnerType();
        final Type otherRawType = other.getRawType();

        return Objects.equals(ownerType, otherOwnerType) && Objects.equals(rawType, otherRawType)
                && Arrays.equals(actualTypeArguments, other.getActualTypeArguments());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(actualTypeArguments) ^ Objects.hashCode(ownerType) ^ Objects.hashCode(rawType);
    }

    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        if (ownerType == null) {
            buf.append(rawType.getName());
        } else {
            if (ownerType instanceof Class) {
                buf.append(((Class<?>) ownerType).getName());
            } else {
                buf.append(ownerType.toString());
            }
            buf.append("$");
            if (ownerType instanceof ParameterizedTypeImpl) {
                final String simpleName = rawType.getName()
                        .replace(((ParameterizedTypeImpl) ownerType).rawType.getName() + "$", "");
                buf.append(simpleName);
            } else {
                buf.append(rawType.getSimpleName());
            }
        }
        if (actualTypeArguments != null && actualTypeArguments.length > 0) {
            buf.append("<");
            boolean first = true;
            for (final Type t : actualTypeArguments) {
                if (first) {
                    first = false;
                } else {
                    buf.append(", ");
                }
                buf.append(t.getTypeName());
            }
            buf.append(">");
        }
        return buf.toString();
    }
}