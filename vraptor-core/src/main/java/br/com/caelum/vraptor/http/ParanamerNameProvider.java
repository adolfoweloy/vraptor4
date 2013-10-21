/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.com.caelum.vraptor.http;

import java.lang.reflect.AccessibleObject;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.thoughtworks.paranamer.AnnotationParanamer;
import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.CachingParanamer;
import com.thoughtworks.paranamer.ParameterNamesNotFoundException;
import com.thoughtworks.paranamer.Paranamer;

/**
 * Paranamer implementation for {@link ParameterNameProvider}, that reads parameter info using Named annotation on each
 * parameter, or read bytecode to find parameter information, in this order.
 *
 * @author Guilherme Silveira
 */
@ApplicationScoped
public class ParanamerNameProvider implements ParameterNameProvider {
	private final Paranamer info = new CachingParanamer(new AnnotationParanamer(new BytecodeReadingParanamer()));

	private static final Logger logger = LoggerFactory.getLogger(ParanamerNameProvider.class);

	@Override
	public List<String> parameterNamesFor(AccessibleObject method) {
		try {
			String[] parameterNames = info.lookupParameterNames(method);
			logger.debug("Found parameter names with paranamer for {} as {}", method, (Object) parameterNames);

			return ImmutableList.copyOf(parameterNames);
		} catch (ParameterNamesNotFoundException e) {
			throw new IllegalStateException("Paranamer were not able to find your parameter names for " + method
					+ "You must compile your code with debug information (javac -g), or using @Named on "
					+ "each method parameter.", e);
		}
	}
}
