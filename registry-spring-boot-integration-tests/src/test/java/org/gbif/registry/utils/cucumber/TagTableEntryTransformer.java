/*
 * Copyright 2020 Global Biodiversity Information Facility (GBIF)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gbif.registry.utils.cucumber;

import org.gbif.api.model.registry.Tag;

import java.util.Map;

import io.cucumber.datatable.TableEntryTransformer;

public class TagTableEntryTransformer implements TableEntryTransformer<Tag> {

  @Override
  public Tag transform(Map<String, String> entry) {
    Tag result = new Tag();
    result.setValue(entry.get("value"));

    return result;
  }
}
