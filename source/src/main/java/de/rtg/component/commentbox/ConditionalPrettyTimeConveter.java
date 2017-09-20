/*
 * Copyright 2017 RTG Portale GmbH (Marcel Trotzek).
 * - Original Copyright 2013 WhiteByte (Nick Russler, Ahmet Yueksektepe).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.rtg.component.commentbox;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

@FacesConverter("de.rtg.ConditionalPrettyTimeConveter")
public class ConditionalPrettyTimeConveter implements Converter {

	private static Converter prettyTimeConverter = null;

	static {
		try {
			prettyTimeConverter = FacesContext.getCurrentInstance().getApplication()
					.createConverter("org.ocpsoft.PrettyTimeConverter");
		} catch (Exception e) {
			// Nothing to do here
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {

		if (prettyTimeConverter != null) {
			return prettyTimeConverter.getAsString(context, component, value);
		} else {
			// Fallback to simple method
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm yyyy");
			return sdf.format((Date) value);
		}
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {

		throw new ConverterException("Does not yet support converting String to Date");
	}
}