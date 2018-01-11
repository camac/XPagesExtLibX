package com.ibm.xsp.extlibx.renderkit.layout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.validator.Validator;

import com.ibm.commons.log.LogMgr;
import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.component.xp.XspInputText;
import com.ibm.xsp.component.xp.XspInputTextarea;
import com.ibm.xsp.extlib.controls.ExtlibControlsLogger;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.extlibx.component.layout.FormGridFriendly;
import com.ibm.xsp.extlibx.component.layout.UIFormGrid;
import com.ibm.xsp.util.FacesUtil;
import com.ibm.xsp.util.TypedUtil;
import com.ibm.xsp.validator.RequiredValidator;
import com.sun.faces.renderkit.html_basic.HtmlBasicRenderer;
import com.sun.faces.util.Util;

public class FormGridRenderer extends HtmlBasicRenderer {

	private static LogMgr logger = ExtlibControlsLogger.CONTROLS;

	public FormGridRenderer() {
		super();
	}

	@Override
	public boolean getRendersChildren() {
		return true;
	}

	@Override
	public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
		if ((context == null) || (component == null)) {
			throw new NullPointerException(Util.getExceptionMessageString("com.sun.faces.NULL_PARAMETERS_ERROR"));
		}

		logger.traceDebug("Begin encoding component '{}'", component.getId());

		if (!component.isRendered()) {
			logger.traceDebug("End encoding component '{}' since rendered attribute is set to false ",
					component.getId());
			return;
		}

		UIFormGrid formGrid = null;

		if (component instanceof UIFormGrid) {
			formGrid = (UIFormGrid) component;
		} else {
			logger.traceDebug("Component is not a Form Grid.. so we will not render it");
			return;
		}

		ResponseWriter writer = context.getResponseWriter();

		String panelType = (String) component.getAttributes().get("panelType");

		writer.startElement("div", component);

		if (formGrid.isLayoutHorizontal()) {
			writer.writeAttribute("class", "form-horizontal", null);
		} else if (formGrid.isLayoutInline()) {
			writer.writeAttribute("class", "form-inline", null);
		}

		writeIdAttributeIfNecessary(context, writer, component);

		writeHeader(writer, component, "fieldset");

		String panelHeader = (String) component.getAttributes().get("panelHeader");
		if (panelHeader != null) {
			writer.startElement("div", component);
			writer.writeAttribute("class", "panel " + panelType, null);
			writer.startElement("div", component);
			writer.writeAttribute("class", "panel-heading", null);
			writer.writeText(panelHeader, null);
			writer.endElement("div");
			writer.startElement("div", component);
			writer.writeAttribute("class", "panel-body", null);
		}
		Util.renderPassThruAttributes(writer, component);
		writeCustomAttributes(writer, component);
	}

	protected void writeHeader(ResponseWriter writer, UIComponent component, String type) throws IOException {

		String header = (String) component.getAttributes().get("header");

		if (StringUtil.isEmpty(header))
			return;

		if (StringUtil.equals(type, "fieldset")) {
			writer.startElement("legend", component);
			writer.writeText(header, null);
			writer.endElement("legend");
		} else {
			writer.startElement("div", component);
			writer.startElement("h3", component);
			writer.writeText(header, null);
			writer.endElement("h3");
			writer.endElement("div");
		}

	}

	@Override
	@SuppressWarnings("unchecked")
	public void encodeChildren(FacesContext context, UIComponent component) throws IOException {

		logger.traceDebug("Begin encoding children for '{}' ", component.getId());

		if ((context == null) || (component == null)) {
			throw new NullPointerException(Util.getExceptionMessageString("com.sun.faces.NULL_PARAMETERS_ERROR"));
		}

		if (!component.isRendered()) {
			logger.traceDebug("End encoding component '{}' since rendered attribute is set to false",
					component.getId());
			return;
		}

		UIFormGrid formGrid = (UIFormGrid) component;
		ResponseWriter writer = context.getResponseWriter();

		// get the mandatory label id's
		List<String> serverSideLabelIds = (List<String>) component.getAttributes().get("serverSideValidationLabelIds");

		Boolean disableErrorSummary = formGrid.isDisableErrorSummary();
				
		// To show all the errors at the bottom
		if (!disableErrorSummary) {
			writeErrorSummary(context, component, writer);
		}

		Iterator<?> kids = getChildren(component);

		if (kids != null) {

			while (kids.hasNext()) {

				UIComponent label = (UIComponent) kids.next();
				UIComponent field = null;

				if (label instanceof FormGridFriendly) {
					field = label;
				} else if (kids.hasNext()) {
					field = (UIComponent) kids.next();
				}

				// Row starts from here
				logger.traceDebug("row started here");
				writeGroupStart(writer);

				boolean required = (fieldRequired(field) || isLabelId(label.getId(), serverSideLabelIds));

				if (label instanceof FormGridFriendly) {
					writeFriendlyLabel(context, writer, formGrid, (FormGridFriendly) label, required);
				} else if (label != null) {
					writeLabel(context, writer, formGrid, label, field, required);
				}
				if (field != null) {
					writeField(context, writer, formGrid, label, field, false);
				}

				// Row ends here
				writeGroupEnd(writer);

				logger.traceDebug("row ends here");

			}
		}

		logger.traceDebug("End encoding children for '{}'", component.getId());
	}

	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {

		if ((context == null) || (component == null)) {
			throw new NullPointerException(Util.getExceptionMessageString("com.sun.faces.NULL_PARAMETERS_ERROR"));
		}
		if (!component.isRendered()) {
			logger.traceDebug("End encoding component '{}' since rendered attribute is set to false ",
					component.getId());
			return;
		}

		ResponseWriter writer = context.getResponseWriter();

		String panelHeader = (String) component.getAttributes().get("panelHeader");
		String panelFooter = (String) component.getAttributes().get("panelFooter");

		if (panelHeader != null && panelFooter != null) {
			writer.endElement("div");
			writer.startElement("div", component);
			writer.writeAttribute("class", "panel-footer", null);
			writer.writeText(panelFooter, null);
			writer.endElement("div");
			writer.endElement("div");
		} else if (panelHeader != null) {
			writer.endElement("div");
			writer.endElement("div");
		}
		writer.endElement("div");
		logger.traceDebug("End encoding component '{}'", component.getId());
	}

	private boolean isLabelId(String id, List<String> serverSideLabelIds) {
		if (serverSideLabelIds != null) {
			for (String lId : serverSideLabelIds) {
				if (lId.equals(id))
					return true;
			}
		}
		return false;
	}

	// to get all error list from UIComponent
	@SuppressWarnings("unchecked")
	public List<String> getErrorList(UIComponent component, FacesContext context) {
		Iterator kids = null;
		List<String> errorMsg = new ArrayList<String>();
		if ((kids = getChildren(component)) != null) {
			while (kids.hasNext()) {
				// UIComponent label = (UIComponent) kids.next();
				UIComponent field = (UIComponent) kids.next();

				if (!(field instanceof UIOutput) && field.getChildCount() > 0) {
					if (kids.hasNext())
						field = (UIComponent) field.getChildren().get(0);
				}
				if (field != null) {
					String clientId = field.getClientId(context);

					Iterator<FacesMessage> iterate = context.getMessages(clientId);
					while (iterate.hasNext()) {
						FacesMessage m = iterate.next();
						errorMsg.add(m.getSummary());
					}
				}
			}
		}
		return errorMsg;
	}

	protected void writeGroupStart(ResponseWriter writer) throws IOException {
		writer.startElement("div", null);
		writer.writeAttribute("class", "form-group", null);
		writer.writeText("\n", null);
	}

	protected void writeLabel(FacesContext context, ResponseWriter writer, UIFormGrid formGrid, UIComponent label,
			UIComponent field, boolean required) throws IOException {

		// if related field is required the label should have red *
		// symbol at the end
		// Label Column starts from here
		String labelSize = formGrid.getLabelSize();

		writer.startElement("label", null);
		if (formGrid.isLayoutHorizontal()) {
			writer.writeAttribute("class", "col-md-" + labelSize + " control-label", null);
		}
		// To show underline bellow the label if title is entered
		if (label.getAttributes().get("title") != null) {
			writer.startElement("u", null);
			writer.writeAttribute("style", " border-bottom: 1px dotted #000; text-decoration: none;", null);
			encodeRecursive(context, label);
			writer.endElement("u");
		} else {
			encodeRecursive(context, label);
		}

		if (required) {
			writer.startElement("span", null);
			writer.writeAttribute("style", "color:red;", null);
			writer.writeText("*", null);
			writer.endElement("span");
		}
		writer.endElement("label");
		// End label column

	}

	protected void writeFriendlyLabel(FacesContext context, ResponseWriter writer, UIFormGrid formGrid,
			FormGridFriendly friendly, boolean required) throws IOException {

		// if related field is required the label should have red *
		// symbol at the end
		// Label Column starts from here
		String labelSize = formGrid.getLabelSize();

		writer.startElement("label", null);

		if (formGrid.isLayoutHorizontal()) {
			writer.writeAttribute("class", "col-md-" + labelSize + " control-label", null);
		} else if (formGrid.isLayoutVertical()) {
			writer.writeAttribute("style", "font-weight: 600;", null);
			writer.writeAttribute("class", "txt-color-blueDark", null);
		}

		writer.writeText(StringUtil.getNonNullString(friendly.getLabel()), null);

		if (required) {
			writer.startElement("span", null);
			writer.writeAttribute("style", "color:red;", null);
			writer.writeText("*", null);
			writer.endElement("span");
		}
		writer.endElement("label");
		// End label column

	}

	protected void writeField(FacesContext context, ResponseWriter writer, UIFormGrid formGrid, UIComponent label,
			UIComponent field, boolean required) throws IOException {

		// Field column starts from heres
		UIComponent myField = null;
		myField = field;
		boolean haserror = false;

		Boolean disableRowError = formGrid.isDisableRowError();

		String fieldSize = formGrid.getFieldSize();

		if (!(myField instanceof UIOutput) && myField.getChildCount() > 0) {
			myField = (UIComponent) myField.getChildren().get(0);
		}
		String clientId = myField.getClientId(context);
		// ErrorIterator

		if (field instanceof XspInputText) {

			XspInputText input = (XspInputText) field;

			String sc = input.getStyleClass();

			if (StringUtil.isEmpty(sc) || sc.contains("form-control")) {
				sc = ExtLibUtil.concatStyleClasses("form-control", input.getStyleClass());
				input.setStyleClass(sc);
			}

		}

		if (field instanceof XspInputTextarea) {

			XspInputTextarea input = (XspInputTextarea) field;

			String sc = input.getStyleClass();

			if (StringUtil.isEmpty(sc) || sc.contains("form-control")) {
				sc = ExtLibUtil.concatStyleClasses("form-control", input.getStyleClass());
				input.setStyleClass(sc);
			}

		}

		Iterator<FacesMessage> it = TypedUtil.getMessages(context, clientId);
		@SuppressWarnings("unchecked")
		Iterator<FacesMessage> iterate = context.getMessages(clientId);
		// if there are errors then set haserror flag to true and
		// add bootstrap has-error attribute to show errors

		if (iterate.hasNext()) {
			haserror = true;
		}

		// if haserror then iterate all errors and write them in
		// responseWriter
		if (haserror) {

			writer.startElement("div", null);

			if (formGrid.isLayoutHorizontal()) {
				writer.writeAttribute("class", "col-md-" + fieldSize + " has-error", null);
			} else {
				writer.writeAttribute("class", " has-error", null);
			}

			encodeRecursive(context, field);

			while (iterate.hasNext()) {
				FacesMessage m = iterate.next();
				if (!disableRowError) { // if disable row error is
					// false by default it is
					// false
					writer.startElement("span", null);
					writer.writeAttribute("class", "help-block", null);
					// encodeRecursive(context, field);
					writer.writeText(m.getSummary(), null);
					writer.endElement("span");
				}
			}
			writer.endElement("div");
		} else {

			if (!formGrid.isLayoutInline()) {

				writer.startElement("div", null);

				if (formGrid.isLayoutHorizontal()) {
					writer.writeAttribute("class", "col-md-" + fieldSize + " form-control-static", null);
				}

			}

			encodeRecursive(context, field);

			// we dont need the bellow if block block becoz we are showing red *
			// infront of
			// label using serversideId's as required field.
			if (required) {
				writer.startElement("p", null);
				writer.writeAttribute("class", "note", null);
				writer.writeText("Required", null);
				writer.endElement("p");
			}

			// Write help
			if (field instanceof FormGridFriendly) {

				String help = ((FormGridFriendly) field).getHelp();

				if (StringUtil.isNotEmpty(help)) {
					writer.startElement("p", null);
					writer.writeAttribute("class", "note", null);
					writer.writeText(help, null);
					writer.endElement("p");
				}

			}

			UIComponent helpFacet = field.getFacet("help");
			if (helpFacet != null) {
				writer.startElement("p", null);
				writer.writeAttribute("class", "note", null);
				FacesUtil.renderChildren(context, helpFacet);
				writer.endElement("p");
			}

			if (!formGrid.isLayoutInline()) {
				writer.endElement("div");
			}
		}

		// Field Columns end here

	}

	protected void writeGroupEnd(ResponseWriter writer) throws IOException {
		writer.endElement("div");
	}

	protected void writeErrorSummary(FacesContext context, UIComponent component, ResponseWriter writer)
			throws IOException {

		List<String> emsg = getErrorList(component, context);

		if (emsg.isEmpty())
			return;

		writer.startElement("div", null);
		writer.writeAttribute("class", "alert alert-danger", null);
		writer.startElement("div", null);
		writer.writeAttribute("class", "text-error", null);
		writer.startElement("span", null);
		writer.writeAttribute("class", "lotusAltText", null);
		writer.writeText("Error:", null);
		writer.endElement("span");
		writer.writeText("Please check the following Errors:", null);
		writer.endElement("div");
		writer.startElement("ul", null);
		for (int err = 0; err < emsg.size(); err++) {
			writer.startElement("li", null);
			writer.writeText(emsg.get(err), null);
			writer.endElement("li");
		}
		writer.endElement("ul");
		writer.endElement("div");

	}

	protected UIInput findInput(UIComponent field) {

		if (field instanceof UIInput) {
			return (UIInput) field;
		} else {
			if (!(field instanceof UIOutput) && field.getChildCount() > 0) {
				UIComponent comp = (UIComponent) field.getChildren().get(0);
				if (comp instanceof UIInput) {
					return (UIInput) comp;
				}
			}
		}
		return null;
	}

	// code to decide whether to show red* infront of field or not
	protected boolean fieldRequired(UIComponent field) {

		if (field == null)
			return false;

		UIInput input = findInput(field);

		if (input == null)
			return false;

		if (input.isRequired())
			return true;

		Validator[] valid = input.getValidators();

		for (Validator v : valid) {
			if (v instanceof RequiredValidator) {
				return true;
			}
		}

		return false;
	}
}
