/**
 * (c) Copyright Ascensio System SIA 2026
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.onlyoffice.slack.shared.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TriFunctionTests {
  @Test
  void whenApply_thenReturnsResult() {
    TriFunction<Integer, Integer, Integer, Integer> sum = (a, b, c) -> a + b + c;
    assertEquals(6, sum.apply(1, 2, 3));
  }

  @Test
  void whenAndThen_thenComposesFunctions() {
    TriFunction<String, String, String, Integer> totalLength =
        (a, b, c) -> a.length() + b.length() + c.length();
    TriFunction<String, String, String, String> describeLength =
        totalLength.andThen(len -> "Total: " + len);
    assertEquals("Total: 8", describeLength.apply("abc", "de", "fgh"));
  }

  @Test
  void whenAndThenWithNullFunction_thenThrowsException() {
    TriFunction<Integer, Integer, Integer, Integer> sum = (a, b, c) -> a + b + c;
    assertThrows(NullPointerException.class, () -> sum.andThen(null));
  }
}
