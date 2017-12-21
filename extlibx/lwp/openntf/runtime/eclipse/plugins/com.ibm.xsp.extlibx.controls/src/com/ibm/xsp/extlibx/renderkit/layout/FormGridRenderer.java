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
import com.ibm.xsp.extlib.controls.ExtlibControlsLogger;
import com.sun.faces.renderkit.html_basic.HtmlBasicRenderer;
import com.sun.faces.util.Util;

public class FormGridRenderer extends HtmlBasicRenderer {

	private static LogMgr logger = ExtlibControlsLogger.CONTROLS;

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
			logger.traceDebug("End encoding component '{}' since rendered attribute is set to false ", component.getId());
			return;
		}

		ResponseWriter writer = context.getResponseWriter();

		String formClass = (String) component.getAttributes().get("formClass");
		String panelType = (String) component.getAttributes().get("panelType");

		writer.startElement("div", component);
		writer.writeAttribute("class", formClass, null);
		writeIdAttributeIfNecessary(context, writer, component);

		// Header
		String header = (String) component.getAttributes().get("header");
		if (header != null) {
			writer.startElement("div", component);
			writer.startElement("h3", component);
			writer.writeText(header, null);
			writer.endElement("h3");
			writer.endElement("div");
		}

		// Panel Header
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

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void encodeChildren(FacesContext context, UIComponent component) throws IOException {

		logger.traceDebug("Begin encoding children for '{}' ", component.getId());

		List<String> serverSideLabelIds = null;
		if ((context == null) || (component == null)) {
			throw new NullPointerException(Util.getExceptionMessageString("com.sun.faces.NULL_PARAMETERS_ERROR"));
		}

		if (!component.isRendered()) {
			logger.traceDebug("End encoding component '{}' since rendered attribute is set to false", component.getId());
			return;
		}

		ResponseWriter writer = context.getResponseWriter();

		// checking to get error list
		List<String> emsg = getErrorList(component, context);
		Iterator kids = null;
		@SuppressWarnings("unused")
		int i = 0;
		// get the mandatory label id's
		serverSideLabelIds = (List<String>) component.getAttributes().get("serverSideValidationLabelIds");
		Boolean disableRowError = (Boolean) component.getAttributes().get("disableRowError");
		Boolean disableErrorSummary = (Boolean) component.getAttributes().get("disableErrorSummary");
		String labelSize = (String) component.getAttributes().get("labelSize");
		String fieldSize = (String) component.getAttributes().get("fieldSize");
		// To show all the errors at the bottom
		if (!disableErrorSummary && emsg.size() > 0) {
			writer.startElement("div", component);
			writer.writeAttribute("class", "alert alert-danger", null);
			writer.startElement("div", component);
			writer.writeAttribute("class", "text-error", null);
			writer.startElement("span", component);
			writer.writeAttribute("class", "lotusAltText", null);
			writer.writeText("Error:", null);
			writer.endElement("span");
			writer.writeText("Please check the following Errors:", null);
			writer.endElement("div");
			writer.startElement("ul", component);
			for (int err = 0; err < emsg.size(); err++) {
				writer.startElement("li", component);
				writer.writeText(emsg.get(err), null);
				writer.endElement("li");
			}
			writer.endElement("ul");
			writer.endElement("div");
		}
		if ((kids = getChildren(component)) != null) {
			while (kids.hasNext()) {
				UIComponent label = (UIComponent) kids.next();
				UIComponent field = null;
				boolean haserror = false;
				if (kids.hasNext()) {
					field = (UIComponent) kids.next();
				}
				boolean isValidator = false;
				// code to decide whether to show red* infront of field or not
				if (field != null) {
					if (field instanceof UIInput) {
						Validator[] valid = ((UIInput) field).getValidators();
						List<Validator> validList = Arrays.asList(valid);
						Iterator iterateValidaList = validList.iterator();
						if (iterateValidaList.hasNext()) {
							isValidator = true;
							iterateValidaList.next();
						}
					} else { // for controls which are included within <xp:span>
						UIComponent myField = null;
						myField = field;
						if (!(myField instanceof UIOutput) && myField.getChildCount() > 0) {
							myField = (UIComponent) myField.getChildren().get(0);
						}
						if (myField instanceof UIInput) {
							Validator[] valid = ((UIInput) myField).getValidators();
							List<Validator> validList = Arrays.asList(valid);
							Iterator iterateValidaList = validList.iterator();
							if (iterateValidaList.hasNext()) {
								isValidator = true;
								iterateValidaList.next();
							}
						}
					}
				}
				// Row starts from here
				logger.traceDebug("Row Started Here");
				writer.startElement("div", component);
				writer.writeAttribute("class", "form-group", null);
				writer.writeText("\n", null);
				if (label != null) {
					// if related field is required the label should have red *
					// symbol at the end
					// Label Column starts from here
					if (isValidator || isLabelId(label.getId(), serverSideLabelIds)) {
						writer.startElement("div", component);
						writer.writeAttribute("class", "col-md-" + labelSize + " control-label", component.getId());
						encodeRecursive(context, label);
						writer.startElement("span", component);
						writer.writeAttribute("style", "color:red;", null);
						writer.writeText("*", null);
						writer.endElement("span");
						writer.endElement("div");
						// End label column
					} else {
						writer.startElement("div", component);
						writer.writeAttribute("class", "col-md-" + labelSize + " control-label", component.getId());
						encodeRecursive(context, label);
						writer.endElement("div");
						// End label column
					}
				}
				if (field != null) {
					// Field column starts from heres
					UIComponent myField = null;
					myField = field;
					if (!(myField instanceof UIOutput) && myField.getChildCount() > 0) {

						myField = (UIComponent) myField.getChildren().get(0);
					}
					String clientId = myField.getClientId(context);
					// ErrorIterator
					Iterator<FacesMessage> iterate = context.getMessages(clientId);
					// if there are errors then set haserror flag to true and
					// add bootstrap has-error attribute to show errors

					if (iterate.hasNext()) {
						haserror = true;
					}
					// if haserror then iterate all errors and write them in
					// responseWriter
					if (haserror) {
						writer.startElement("div", component);
						writer.writeAttribute("class", "col-md-" + fieldSize + " has-error", null);
						encodeRecursive(context, field);
						while (iterate.hasNext()) {
							FacesMessage m = iterate.next();
							if (!disableRowError) { // if disable row error is
								// false by default it is
								// false
								writer.startElement("span", component);
								writer.writeAttribute("class", "help-block", null);
								// encodeRecursive(context, field);
								writer.writeText(m.getSummary(), null);
								writer.endElement("span");
							}
						}
						writer.endElement("div");
					} else {
						writer.startElement("div", component);
						writer.writeAttribute("class", "col-md-" + fieldSize + " form-control-static", null);
						encodeRecursive(context, field);
						writer.endElement("div");
					}
					// Field Columns end here
				}

				// Row ends here
				writer.endElement("div");
				logger.traceDebug("Row Ends Here");
				i++;
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
			logger.traceDebug("End encoding component '{}' since rendered attribute is set to false ", component.getId());
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
	@SuppressWarnings({ "unchecked", "rawtypes" })
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
}
