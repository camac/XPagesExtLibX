package com.ibm.xsp.extlibx.renderkit.html_extended.form;

import java.io.IOException;
import java.util.HashMap;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.commons.util.io.json.JsonException;
import com.ibm.commons.util.io.json.JsonGenerator;
import com.ibm.commons.util.io.json.JsonJavaFactory;
import com.ibm.xsp.component.FacesAttrsObject;
import com.ibm.xsp.component.UIViewRootEx;
import com.ibm.xsp.extlibx.component.form.MasterCheckbox;
import com.ibm.xsp.extlibx.resources.ExtLibXResources;
import com.ibm.xsp.renderkit.ReadOnlyAdapterRenderer;
import com.ibm.xsp.renderkit.html_basic.AttrsUtil;
import com.ibm.xsp.renderkit.html_basic.SelectManyCheckboxListRenderer;
import com.ibm.xsp.util.JavaScriptUtil;

public class MasterCheckboxRenderer extends SelectManyCheckboxListRenderer {

	public static final String RENDERER_TYPE = "Checkbox.Master";

	public MasterCheckboxRenderer() {

	}

	@Override
	public void encodeBegin(FacesContext context, UIComponent component) throws IOException {

		UIViewRootEx rootEx = (UIViewRootEx) context.getViewRoot();
		ExtLibXResources.addEncodeResource(rootEx, ExtLibXResources.extlibMasterCheckbox);

		super.encodeBegin(context, component);

	}

	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {

		// super.encodeEnd(context, component);

		ResponseWriter writer = context.getResponseWriter();

		writer.startElement("div", component); // $NON-NLS-1$
		writer.startElement("input", component);
		writer.writeAttribute("type", "checkbox", "type");
		String clientId = component.getClientId(context);
		writer.writeAttribute("id", clientId + "_MASTER", "id");
		writer.writeAttribute("name", clientId + "_MASTER", "name");

		// read only
		boolean readonly = ReadOnlyAdapterRenderer.isReadOnly(context, component);
		if (readonly) {
			writer.writeAttribute("readonly", "readonly", "readonly"); // $NON-NLS-1$
																		// $NON-NLS-2$
																		// $NON-NLS-3$
			// TODO when showReadonlyAsDisabled, writing the disabled attribute.
			// would it be OK to only write the readonly attribute?
			writer.writeAttribute("disabled", "disabled", "disabled"); // $NON-NLS-1$
																		// $NON-NLS-2$
																		// $NON-NLS-3$
		}

		// writer.writeAttribute(CHECKED, UIInputCheckbox.isChecked(context,
		// component) ? Boolean.TRUE : Boolean.FALSE,
		// VALUE);
		if (!readonly) { // when readonly will have written disabled above.
			// check if disabled, and if so, write the disabled attribute
			Object localObject = component.getAttributes().get("disabled");
			if ((localObject != null) && ((localObject instanceof Boolean))
					&& (((Boolean) localObject).booleanValue())) {
				writer.writeAttribute("disabled", "disabled", "disabled");
			}
		}

		// encodeHtmlAttributes(writer, component, false);

		// String onchangeTrigger = null;
		// if (context instanceof FacesContextEx) {
		// FacesContextEx contextEx = (FacesContextEx) context;
		// // xsp.client.script.radioCheckbox.ie.onchange.trigger=
		// // early-onclick | late-onblur[default]
		// onchangeTrigger =
		// contextEx.getProperty(XspPropertyConstants.XSP_RADIO_ONCHANGE_TRIGGER);
		// }
		// if (null != onchangeTrigger &&
		// !"late-onblur".equals(onchangeTrigger)) { //$NON-NLS-1$
		// writer.writeAttribute("onchangeTrigger", onchangeTrigger, null);
		// //$NON-NLS-1$
		// }

		if (component instanceof FacesAttrsObject) {
			FacesAttrsObject attrsHolder = (FacesAttrsObject) component;
			AttrsUtil.encodeAttrs(context, writer, attrsHolder);
		}

		writer.endElement("input");

		// end the div
		writer.endElement("div"); // $NON-NLS-1$

		encodeScript(context, component);

	}

	public void encodeScript(FacesContext context, UIComponent component) throws IOException {

		// ResponseWriter w = context.getResponseWriter();
		String clientId = component.getClientId(context);

		StringBuilder sb = new StringBuilder();

		sb.append(" try { var existing = dijit.byId('" + clientId
				+ "'); if (existing) { existing.destroy(); }; } catch (e) {}; ");

		// create the parameters to initialize a new select2 object
		HashMap<String, Object> params = new HashMap<String, Object>();

		// will use it to register the widget
		params.put("id", clientId);
		params.put("masterId", clientId + "_MASTER");

		if (component instanceof MasterCheckbox) {

			MasterCheckbox cb = (MasterCheckbox) component;
			String parentSelectorClick = cb.getParentSelectorClick();
			if (StringUtil.isNotEmpty(parentSelectorClick)) {
				params.put("parentSelectorClick", parentSelectorClick);
			}

			String parentSelectorStyleClass = cb.getParentSelectorStyleClass();
			if (StringUtil.isNotEmpty(parentSelectorStyleClass)) {
				params.put("parentSelectorStyleClass", parentSelectorStyleClass);
			}

			String styleClassChecked = cb.getParentStyleClassChecked();
			if (StringUtil.isNotEmpty(styleClassChecked)) {
				params.put("parentStyleClassChecked", styleClassChecked);
			}
			String styleClassUnchecked = cb.getParentStyleClassUnchecked();
			if (StringUtil.isNotEmpty(styleClassUnchecked)) {
				params.put("parentStyleClassUnchecked", styleClassUnchecked);
			}

		}

		try {

			sb.append("new extlib.dijit.MasterCheckbox("
					+ JsonGenerator.toJson(JsonJavaFactory.instanceEx, params, false) + ");");

		} catch (JsonException e) {
		}

		JavaScriptUtil.addScriptOnLoad(sb.toString());

	}

}
