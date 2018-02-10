package com.ibm.xsp.extlibx.actions.server;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.MethodNotFoundException;
import javax.faces.el.ValueBinding;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.FacesExceptionEx;
import com.ibm.xsp.extlib.actions.server.AbstractServerSimpleAction;
import com.ibm.xsp.extlib.component.dialog.UIDialog;
import com.ibm.xsp.extlib.component.dynamiccontent.UIInPlaceForm;
import com.ibm.xsp.util.FacesUtil;

public class DialogAction extends AbstractServerSimpleAction {

	private String _for;
	private String _dialogAction;

	private static final String ACTION_SHOW = "show"; // $NON-NLS-1$
	private static final String ACTION_HIDE = "hide"; // $NON-NLS-1$

	@Override
	public Object invoke(FacesContext context, Object[] params) throws EvaluationException, MethodNotFoundException {

		String forId = getFor();

		UIDialog dialog = findNonNullDialog(forId);

		String action = getDialogAction();

		if (StringUtil.equals(action, ACTION_SHOW)) {
			dialog.show();
		} else if (StringUtil.equals(action, ACTION_HIDE)) {
			dialog.hide();
		} else {
			dialog.hide();
		}

		return null; // do not move to a different page
	}

	/**
	 * @param forId
	 * @return
	 */
	private UIDialog findNonNullDialog(String forId) {

		UIComponent forControl = null;
		// If there is an explicit id, then use it
		if (StringUtil.isNotEmpty(forId)) {

			forControl = FacesUtil.getComponentFor(getComponent(), forId);

			if (null != forControl && !(forControl instanceof UIDialog)) {
				String msg = com.ibm.xsp.extlibx.controls.ResourceHandler
						.getString("DialogAction.TheDialogactioncannotchanget"); // $NLS-InPlaceFormAction.TheInPlaceFormactioncannotchanget-1$
				msg = StringUtil.format(msg, forId);
				throw new FacesExceptionEx(msg);
			}
			if (forControl == null) {
				String msg = com.ibm.xsp.extlibx.controls.ResourceHandler
						.getString("DialogAction.TheDialogactioncannotfindaco"); // $NLS-InPlaceFormAction.TheInPlaceFormactioncannotfindaco-1$
				msg = StringUtil.format(msg, forId);
				throw new FacesExceptionEx(msg);
			}
			return (UIDialog) forControl;
		} else {
			for (UIComponent c = getComponent(); c != null; c = c.getParent()) {
				if (c instanceof UIDialog) {
					forControl = c;
					break;
				}
			}
			if (forControl == null) {
				String msg = com.ibm.xsp.extlibx.controls.ResourceHandler
						.getString("DialogAction.TheDialogactioncannotfindana"); // $NLS-InPlaceFormAction.TheInPlaceFormactioncannotfindana-1$
				throw new FacesExceptionEx(msg);
			}
			return (UIDialog) forControl;
		}
	}

	/**
	 * The ID of the {@link UIInPlaceForm} control whose state will be updated
	 * by this action. (Note, this is the control's ID, not the clientId.) + *
	 *
	 * @return the _for
	 */
	public String getFor() {
		if (null != _for) {
			return _for;
		}
		ValueBinding vb = getValueBinding("for"); //$NON-NLS-1$
		if (null != vb) {
			return (String) vb.getValue(getFacesContext());
		}
		return null;
	}

	/**
	 * + * @param _for the ID of the {@link UIInPlaceForm} control.
	 */
	public void setFor(String _for) {
		this._for = _for;
	}

	/**
	 * The action to perform on the InPlaceForm
	 *
	 * @return
	 */
	public String getDialogAction() {
		if (null != _dialogAction) {
			return _dialogAction;
		}
		ValueBinding vb = getValueBinding("dialogAction"); //$NON-NLS-1$
		if (null != vb) {
			return (String) vb.getValue(getFacesContext());
		}
		return null;

	}

	public void setDialogAction(String _dialogAction) {
		this._dialogAction = _dialogAction;
	}

	@Override
	public void restoreState(FacesContext context, Object value) {
		Object[] state = (Object[]) value;
		super.restoreState(context, state[0]);
		_for = (String) state[1];
		_dialogAction = (String) state[2];
	}

	@Override
	public Object saveState(FacesContext context) {
		Object[] state = new Object[3];
		state[0] = super.saveState(context);
		state[1] = _for;
		state[2] = _dialogAction;
		return state;
	}

}
