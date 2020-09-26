package celuk.language;

/**
 * Copyright 2009 by dueni.ch
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

import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Implements an Enumeration that combines elements from a Set and an
 * Enumeration. Used by {@link MultiplePropertiesResourceBundle}.
 */
class ResourceBundleEnumeration implements Enumeration<String> {

	private Set<String> set;
	private Iterator<String> iterator;
	private Enumeration<String> enumeration;
	private String next = null;

	/**
	 * Constructs a resource bundle enumeration.
	 * 
	 * @param set
	 *          a set providing some elements of the enumeration
	 * @param enumeration
	 *          an enumeration providing more elements of the enumeration.
	 *          enumeration may be null.
	 */
	ResourceBundleEnumeration(Set<String> set, Enumeration<String> enumeration) {
		this.set = set;
		this.iterator = set.iterator();
		this.enumeration = enumeration;
	}


	public boolean hasMoreElements() {
		if (next == null) {
			if (iterator.hasNext()) {
				next = iterator.next();
			} else if (enumeration != null) {
				while (next == null && enumeration.hasMoreElements()) {
					next = enumeration.nextElement();
					if (set.contains(next)) {
						next = null;
					}
				}
			}
		}
		return next != null;
	}

	public String nextElement() {
		if (hasMoreElements()) {
			String result = next;
			next = null;
			return result;
		} else {
			throw new NoSuchElementException();
		}
	}
}

