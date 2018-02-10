package com.ibm.xsp.extlibx.actions.client;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.MethodNotFoundException;
import javax.faces.el.ValueBinding;

import com.ibm.xsp.actions.client.AbstractClientSimpleAction;
import com.ibm.xsp.util.FacesUtil;

public class OpenTooltipDialogClientAction extends AbstractClientSimpleAction {

	private String dialogId;
	private String targetId;

	private String getScript(FacesContext context) {

		String dId = getDialogId();
		String tId = getTargetId();

		UIComponent dialog = FacesUtil.getComponentFor(getComponent(), dId);
		UIComponent target = FacesUtil.getComponentFor(getComponent(), tId);

		String dialogClientId = dialog.getClientId(context);
		String targetClientId = target.getClientId(context);

		String script = String.format("XSP.openTooltipDialog('%s','%s');", dialogClientId, targetClientId);

		return script;
	}

	public String getDialogId() {

		if (this.dialogId != null) {
			return this.dialogId;
		}

		ValueBinding vb = getValueBinding("dialogId");

		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
		}

		return null;

	}

	public void setDialogId(String dialogId) {
		this.dialogId = dialogId;
	}

	public String getTargetId() {

		if (this.targetId != null) {
			return this.targetId;
		}

		ValueBinding vb = getValueBinding("targetId");

		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
		}

		return null;

	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	@Override
	public Object invoke(FacesContext context, Object[] arg1) throws EvaluationException, MethodNotFoundException {

		return getScript(context);
	}

	@Override
	public void restoreState(FacesContext context, Object state) {

		Object[] values = (Object[]) state;

		super.restoreState(context, values[0]);

		targetId = (String) values[1];
		dialogId = (String) values[2];

	}

	@Override
	public Object saveState(FacesContext context) {

		Object[] values = new Object[3];

		values[0] = super.saveState(context);
		values[1] = targetId;
		values[2] = dialogId;

		return values;

	}

}
