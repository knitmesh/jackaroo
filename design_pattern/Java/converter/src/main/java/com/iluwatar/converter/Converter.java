/**
 * The MIT License
 * Copyright (c) 2014-2016 Ilkka Seppälä
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.iluwatar.converter;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Generic converter, thanks to Java8 features not only provides a way of generic bidirectional
 * conversion between coresponding types, but also a common way of converting a collection of objects
 * of the same type, reducing boilerplate code to the absolute minimum.
 * @param <T> DTO representation's type
 * @param <U> Domain representation's type
 */
public class Converter<T, U> {

  private final Function<T, U> fromDto;
  private final Function<U, T> fromEntity;

  /**
   * @param fromDto Function that converts given dto entity into the domain entity.
   * @param fromEntity Function that converts given domain entity into the dto entity.
   */
  public Converter(final Function<T, U> fromDto, final Function<U, T> fromEntity) {
    this.fromDto = fromDto;
    this.fromEntity = fromEntity;
  }

  /**
   * @param userDto DTO entity
   * @return The domain representation - the result of the converting function application on dto entity.
   */
  public final U convertFromDto(final T userDto) {
    return fromDto.apply(userDto);
  }

  /**
   * @param user domain entity
   * @return The DTO representation - the result of the converting function application on domain entity.
   */
  public final T convertFromEntity(final U user) {
    return fromEntity.apply(user);
  }

  /**
   * @param dtoUsers collection of DTO entities
   * @return List of domain representation of provided entities retrieved by
   *        mapping each of them with the convertion function
   */
  public final List<U> createFromDtos(final Collection<T> dtoUsers) {
    return dtoUsers.stream().map(this::convertFromDto).collect(Collectors.toList());
  }

  /**
   * @param users collection of domain entities
   * @return List of domain representation of provided entities retrieved by
   *        mapping each of them with the convertion function
   */
  public final List<T> createFromEntities(final Collection<U> users) {
    return users.stream().map(this::convertFromEntity).collect(Collectors.toList());
  }

}
