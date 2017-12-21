package com.ibm.xsp.extlibx.component.layout;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.component.UIPanelEx;

public class UIFormGrid extends UIPanelEx {

	public static final String STYLEKIT_FAMILY = "FormGrid";

	public static final String RENDERER_TYPE = "com.ibm.xsp.extlibx.FormGrid";
	
	public static final String LAYOUT_HORIZONTAL = "horizontal";
	public static final String LAYOUT_INLINE = "inline";
	public static final String LAYOUT_VERTICAL = "vertical";

	private String layout = null;

	private String formClass = null;
	private Boolean disableRowError = false;
	private Boolean disableErrorSummary = false;
	private String panelHeader = null;
	private String panelFooter = null;
	private String panelType = null;
	private String labelSize = null;
	private String fieldSize = null;
	private String header = null;
	private List<String> serverSideValidationLabelIds = new ArrayList<String>();

	public UIFormGrid() {
		super();
		setRendererType(RENDERER_TYPE);
	}

	@Override
	public String getRendererType() {
		return super.getRendererType();
	}

	@Override
	public void setRendererType(String rendererType) {
		super.setRendererType(rendererType);
	}

	@Override
	public String getStyleKitFamily() {
		return "FormGrid";

	}

	@SuppressWarnings("unchecked")
	@Override
	public void restoreState(FacesContext fc, Object object) {

		// cast the object as the object array
		Object[] state = (Object[]) object;

		// restore properties back to where they
		this.formClass = (String) state[1];
		this.disableRowError = (Boolean) state[2];
		this.disableErrorSummary = (Boolean) state[3];
		this.panelHeader = (String) state[4];
		this.panelFooter = (String) state[5];
		this.panelType = (String) state[6];
		this.labelSize = (String) state[7];
		this.fieldSize = (String) state[8];
		this.header = (String) state[9];
		this.serverSideValidationLabelIds = (List<String>) state[10];
		this.layout = (String) state[11];
		// tell the super class it can restore from first position of the state
		super.restoreState(fc, state[0]);
	}

	@Override
	public Object saveState(FacesContext fc) {

		// Declare array of size parameters+1
		Object[] state = new Object[12];

		// tell super class its state in first position in array
		state[0] = super.saveState(fc);
		// save out properties in the rest
		state[1] = this.formClass;
		state[2] = this.disableRowError;
		state[3] = this.disableErrorSummary;
		state[4] = this.panelHeader;
		state[5] = this.panelFooter;
		state[6] = this.panelType;
		state[7] = this.labelSize;
		state[8] = this.fieldSize;
		state[9] = this.header;
		state[10] = this.serverSideValidationLabelIds;
		state[11] = this.layout;
		// return the state object
		return state;
	}

	public String getLayout() {

		if (this.layout != null) {
			return this.layout;
		}

		ValueBinding vb = getValueBinding("layout");

		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
		}

		return LAYOUT_HORIZONTAL;

	}

	public void setLayout(String formLayout) {
		this.layout = formLayout;
	}

	public boolean isLayoutHorizontal() {
		return StringUtil.equals(getLayout(), LAYOUT_HORIZONTAL);
	}

	public boolean isLayoutVertical() {
		return StringUtil.equals(getLayout(), LAYOUT_VERTICAL);
	}

	public boolean isLayoutInline() {
		return StringUtil.equals(getLayout(), LAYOUT_INLINE);
	}

	public String getFormClass() {

		if (this.formClass != null) {

			if (this.formClass.equals("normal")) {
				return "";
			}

			return this.formClass;

		}

		ValueBinding vb = getValueBinding("formClass");

		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
		} else {
			return "form-horizontal"; // default bootstrap form property
		}

	}

	public void setFormClass(String formClass) {
		this.formClass = formClass;
	}

	public Boolean getDisableRowError() {
		return isDisableRowError();
	}

	public Boolean isDisableRowError() {

		if (this.disableRowError != null) {
			return this.disableRowError;
		}
		ValueBinding vb = getValueBinding("disableRowError");
		if (vb != null) {
			return (Boolean) vb.getValue(getFacesContext());
		}
		return null;
	}

	public void setDisableRowError(Boolean disableRowError) {
		this.disableRowError = disableRowError;
	}

	public Boolean getDisableErrorSummary() {
		return isDisableErrorSummary();
	}

	public Boolean isDisableErrorSummary() {

		if (this.disableErrorSummary != null) {
			return this.disableErrorSummary;
		}
		ValueBinding vb = getValueBinding("disableErrorSummary");
		if (vb != null) {
			return (Boolean) vb.getValue(getFacesContext());
		}

		return null;
	}

	public void setDisableErrorSummary(Boolean disableErrorSummary) {
		this.disableErrorSummary = disableErrorSummary;
	}

	public String getPanelHeader() {

		if (this.panelHeader != null) {
			return this.panelHeader;
		}
		ValueBinding vb = getValueBinding("panelHeader");
		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
		}

		return null;
	}

	public void setPanelHeader(String panelHeader) {
		this.panelHeader = panelHeader;
	}

	public String getPanelFooter() {

		if (this.panelFooter != null) {
			return this.panelFooter;
		}
		ValueBinding vb = getValueBinding("panelFooter");
		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
		}

		return null;
	}

	public void setPanelFooter(String panelFooter) {
		this.panelFooter = panelFooter;
	}

	public String getPanelType() {

		if (this.panelType != null) {
			return this.panelType;
		}
		ValueBinding vb = getValueBinding("panelType");
		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
		} else {
			return "panel-default";
		}

	}

	public void setPanelType(String panelType) {
		this.panelType = panelType;
	}

	public String getLabelSize() {

		if (this.labelSize != null) {
			return this.labelSize;
		}

		ValueBinding vb = getValueBinding("labelSize");

		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
		} else {

			return "3"; // default bootstrap form property
		}
	}

	public void setLabelSize(String labelSize) {
		this.labelSize = labelSize;
	}

	public String getFieldSize() {

		if (this.fieldSize != null) {
			return this.fieldSize;
		}

		ValueBinding vb = getValueBinding("fieldSize");

		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
		} else {

			return "9"; // default bootstrap form property
		}
	}

	public void setFieldSize(String fieldSize) {
		this.fieldSize = fieldSize;
	}

	public String getHeader() {

		if (this.header != null) {
			return this.header;
		}

		ValueBinding vb = getValueBinding("header");

		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
		}
		return null;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	@SuppressWarnings("unchecked")
	public List<String> getServerSideValidationLabelIds() {

		if (this.serverSideValidationLabelIds != null) {
			return this.serverSideValidationLabelIds;
		}
		ValueBinding vb = getValueBinding("serverSideValidationLabelIds");
		if (vb != null) {
			return (List<String>) vb.getValue(getFacesContext());

		}
		return null;
	}

	public void setServerSideValidationLabelIds(List<String> serverSideValidationLabelIds) {
		this.serverSideValidationLabelIds = serverSideValidationLabelIds;
	}

	public void addString(String str) {
		if (StringUtil.isEmpty(str)) {
			serverSideValidationLabelIds = new ArrayList<String>();
		}
		this.serverSideValidationLabelIds.add(str);
	}

}
