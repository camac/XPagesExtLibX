package com.ibm.xsp.extlibx.renderkit.html_extended.form;

import java.io.IOException;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.ajax.AjaxUtil;
import com.ibm.xsp.application.XspPropertyConstants;
import com.ibm.xsp.component.FacesAttrsObject;
import com.ibm.xsp.component.FacesOutputFiltering;
import com.ibm.xsp.component.UIInputCheckbox;
import com.ibm.xsp.component.UISelectManyCheckbox;
import com.ibm.xsp.component.xp.XspInputCheckbox;
import com.ibm.xsp.context.FacesContextEx;
import com.ibm.xsp.designer.context.XSPContext;
import com.ibm.xsp.extlibx.component.form.MasterCheckbox;
import com.ibm.xsp.extlibx.component.form.SlaveCheckbox;
import com.ibm.xsp.renderkit.ReadOnlyAdapterRenderer;
import com.ibm.xsp.renderkit.dojo.DojoUtil;
import com.ibm.xsp.renderkit.html_basic.AttrsUtil;
import com.ibm.xsp.renderkit.html_basic.InputRendererUtil;
import com.ibm.xsp.renderkit.html_extended.CheckboxRenderer;
import com.ibm.xsp.util.FacesUtil;

public class SlaveCheckboxRenderer extends CheckboxRenderer {

	public static final String XSPPROP_STYLE = "smartadmin.checkbox.style";

	public SlaveCheckboxRenderer() {
		super();
	}

	static final String CHECKBOX = "checkbox"; //$NON-NLS-1$
	static final String[] ATTRS = { "tabindex", "accesskey" }; // $NON-NLS-1$
																// $NON-NLS-2$
	static final String DIR = "dir"; //$NON-NLS-1$

	private String getStyle(FacesContext context, UIComponent component) {

		String style = null;

		if (component instanceof XspInputCheckbox) {
			style = ((XspInputCheckbox) component).getStyleClass();

			if (StringUtil.isNotEmpty(style)) {
				return style;
			}
		}

		style = XSPContext.getXSPContext(context).getProperty(XSPPROP_STYLE);

		if (StringUtil.isNotEmpty(style)) {
			return style;
		}

		return null;
	}

	private String getName(FacesContext context, UIComponent component) {

		if (component instanceof SlaveCheckbox) {

			String masterId = ((SlaveCheckbox) component).getMaster();

			if (StringUtil.isNotEmpty(masterId)) {

				UIComponent master = FacesUtil.getComponentFor(component, masterId);

				if (master != null && master instanceof UISelectManyCheckbox) {
					return master.getClientId(context);
				}
			}

		}

		return component.getClientId(context);

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * javax.faces.render.Renderer#encodeEnd(javax.faces.context.FacesContext,
	 * javax.faces.component.UIComponent)
	 */
	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {

		String style = getStyle(context, component);

		boolean styled = StringUtil.isNotEmpty(style);

		// validate the context and component
		if (context == null || component == null) {
			throw new NullPointerException();
		}
		ResponseWriter writer = context.getResponseWriter();
		if (writer == null) {
			throw new NullPointerException();
		}

		// should this component be rendered
		if (!component.isRendered()) {
			return;
		}

		// SPR# YSAI8RVAJB - do not render a checkbox for non-existent entries
		// (e.g. deleted parent docs)
		String customHtmlValue = null;
		XspInputCheckbox checkbox = (component instanceof XspInputCheckbox) ? (XspInputCheckbox) component : null;
		if (null != checkbox && checkbox.useCustomHtmlValue()) {
			customHtmlValue = checkbox.getCustomHtmlValue();
			if (StringUtil.isEmpty(customHtmlValue)) {
				return; // we need a custom value and we don't have it - return
			} // if non-null, the customHtmlValue is rendered further down
				// but we need to check here first in case we need to bale...
		}

		// start the label
		String text = (String) component.getAttributes().get(TEXT);
		if (text != null && component instanceof FacesOutputFiltering) {
			String filter = ((FacesOutputFiltering) component).getHtmlFilterName();
			if (filter != null) {
				text = ((FacesContextEx) context).filterHtml(filter, text);
			}
		}
		boolean writeLabel = StringUtil.isNotEmpty(text);

		if (AjaxUtil.isAjaxPartialRefresh(context) && AjaxUtil.isRendered(context, component)) {
			// then this is a 'partial refresh' targetted on the component
			// the JS will replace the <INPUT> element, so don't render the
			// <LABEL> or we'll get nested labels
			// don't move the ID to the <LABEL> element as this will affect
			// other functionality
			writeLabel = false;
		}

		// start the wrapping div tag
		if (styled) {
			writer.startElement(LABEL, component);
			writer.writeAttribute("class", "checkbox-inline", "class");

		} else {// } else if (writeLabel) {

			writer.startElement("div", component); // $NON-NLS-1$
			if (checkbox.getDojoType() == null) {
				writer.writeAttribute("class", CHECKBOX, "class"); // $NON-NLS-1$
																	// $NON-NLS-2$
			} else {
				writer.writeAttribute("style", "display: inline;", "style"); // $NON-NLS-1$
																				// $NON-NLS-2$
																				// $NON-NLS-3$
			}
			encodeHtmlStyleAttributes(writer, component);

			writer.startElement(LABEL, component);
			String clientId = component.getClientId(context);
			writer.writeAttribute(FOR, clientId, CLIENTID);
			String dir = (String) component.getAttributes().get(DIR);
			if (!StringUtil.isEmpty(dir))
				writer.writeAttribute(DIR, dir, DIR);
			encodeHtmlStyleAttributes(writer, component);
		}

		// encode the input tag
		writer.startElement(INPUT, component);

		// dojoType and dojoAttributes if they are present
		boolean forceId = false;
		if (checkbox != null) {
			// Only force the Id if dojoType is set
			if (DojoUtil.addDojoAttributes(context, checkbox) != null) {
				forceId = true;
			}
		}

		writeId(writer, context, component, forceId);

		writer.writeAttribute(TYPE, CHECKBOX, TYPE);

		String name = getName(context, component);
		writer.writeAttribute(NAME, name, NAME);

		if (styled) {
			writer.writeAttribute("class", "checkbox " + style, "class");
		}

		// readonly
		boolean readonly = ReadOnlyAdapterRenderer.isReadOnly(context, component);
		if (readonly) {
			writer.writeAttribute("readonly", "readonly", "readonly"); // $NON-NLS-1$
																		// $NON-NLS-2$
																		// $NON-NLS-3$
			// when showReadonlyAsDisabled, writing the disabled attribute.
			// would it be OK to only write the readonly attribute?
			writer.writeAttribute("disabled", "disabled", "disabled"); // $NON-NLS-1$
																		// $NON-NLS-2$
																		// $NON-NLS-3$
		}

		// Add ARIA role attribute

		if (null != checkbox) {
			String role = checkbox.getRole();
			if (StringUtil.isNotEmpty(role)) {
				writer.writeAttribute("role", role, "role"); // $NON-NLS-1$
																// $NON-NLS-2$
			}

			if (checkbox.isRequired()) {
				writer.writeAttribute("aria-required", "true", null); // $NON-NLS-1$
																		// $NON-NLS-2$
			}
			if (!checkbox.isValid()) {
				writer.writeAttribute("aria-invalid", "true", null); // $NON-NLS-1$
																		// $NON-NLS-2$
			}
		}

		// encode the attributes listed in ATTRS...
		for (int i = 0; i < ATTRS.length; i++)
			writeAttribute(writer, component, ATTRS[i]);

		// encodeHtmlStyleAttributes(writer, component);

		// add the customHtmlValue if non-null (typically the note id)
		if (StringUtil.isNotEmpty(customHtmlValue)) {
			writer.writeAttribute(VALUE, customHtmlValue, VALUE);
		}

		if (component instanceof SlaveCheckbox) {

			SlaveCheckbox jic = ((SlaveCheckbox) component);

			String masterId = ((SlaveCheckbox) component).getMaster();
			if (StringUtil.isNotEmpty(masterId)) {

				UIComponent mastercb = FacesUtil.getComponentFor(component, masterId);

				if (mastercb instanceof MasterCheckbox) {

					Object o = ((MasterCheckbox) mastercb).getValue();

					if (o != null && o instanceof List) {

						@SuppressWarnings("rawtypes")
						boolean checked = ((List) o).contains(jic.getCheckedValue());
						writer.writeAttribute(CHECKED, checked ? Boolean.TRUE : Boolean.FALSE, VALUE);

					}

				}

			}

		}

		writer.writeAttribute(CHECKED, UIInputCheckbox.isChecked(context, component) ? Boolean.TRUE : Boolean.FALSE,
				VALUE);
		if (!readonly) { // when readonly will have written disabled above.
			// check if disabled, and if so, write the disabled attribute
			writeBooleanAttribute(writer, component, "disabled"); //
		}

		encodeHtmlAttributes(writer, component, false);

		String onchangeTrigger = null;
		if (context instanceof FacesContextEx) {
			FacesContextEx contextEx = (FacesContextEx) context;
			// xsp.client.script.radioCheckbox.ie.onchange.trigger=
			// early-onclick | late-onblur[default]
			onchangeTrigger = contextEx.getProperty(XspPropertyConstants.XSP_RADIO_ONCHANGE_TRIGGER);
		}
		if (null != onchangeTrigger && !"late-onblur".equals(onchangeTrigger)) { //$NON-NLS-1$
			writer.writeAttribute("onchangeTrigger", onchangeTrigger, null); //$NON-NLS-1$
		}

		if (component instanceof FacesAttrsObject) {
			FacesAttrsObject attrsHolder = (FacesAttrsObject) component;
			AttrsUtil.encodeAttrs(context, writer, attrsHolder);
		}

		writer.endElement(INPUT);

		// end the label
		if (styled) {

			writer.startElement("span", component);
			if (StringUtil.isNotEmpty(text)) {
				writer.writeText(text, TEXT);
			}
			writer.endElement("span");

			writer.endElement(LABEL);

		} else {
			// end the div
			writer.endElement("div"); // $NON-NLS-1$
			if (writeLabel) {
				writer.writeText(text, TEXT);
				writer.endElement(LABEL);
			}
		}

		InputRendererUtil.encodeValidation(context, context.getResponseWriter(), (UIInput) component);

		InputRendererUtil.encodeDirtyState(context, context.getResponseWriter(), (UIInput) component);
	}

}
