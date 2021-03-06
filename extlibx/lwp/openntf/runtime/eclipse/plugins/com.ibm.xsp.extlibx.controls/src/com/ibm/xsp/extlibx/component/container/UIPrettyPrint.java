package com.ibm.xsp.extlibx.component.container;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.component.UIPanelEx;
import com.ibm.xsp.util.HtmlUtil;

public class UIPrettyPrint extends UIPanelEx {

	private String pageName = null;

	private Boolean lineNumbers = null;
	private String languageHint = null;

	public UIPrettyPrint() {
		setTagName("pre");
		setStyleClass("prettyprint");
	}

	public String getPageName() {

		if (this.pageName != null) {
			return this.pageName;
		}

		ValueBinding vb = getValueBinding("pageName");

		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
		}

		return null;

	}

	public void setPageName(String pageName) {
		this.pageName = pageName;
	}

	public String getLanguageHint() {

		if (this.languageHint != null) {
			return this.languageHint;
		}

		ValueBinding vb = getValueBinding("languageHint");

		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
		}

		return null;

	}

	public void setLanguageHint(String languageHint) {
		this.languageHint = languageHint;
	}

	public Boolean isLineNumbers() {

		if (this.lineNumbers != null) {
			return this.lineNumbers;
		}

		ValueBinding vb = getValueBinding("lineNumbers");

		if (vb != null) {
			return (Boolean) vb.getValue(getFacesContext());
		}

		return false;

	}

	public void setLineNumbers(Boolean lineNumbers) {
		this.lineNumbers = lineNumbers;
	}

	@Override
	public void restoreState(FacesContext context, Object state) {

		Object[] values = (Object[]) state;

		super.restoreState(context, values[0]);

		pageName = (String) values[1];
		languageHint = (String) values[2];
		lineNumbers = (Boolean) values[3];

	}

	@Override
	public Object saveState(FacesContext context) {

		Object[] values = new Object[4];

		values[0] = super.saveState(context);
		values[1] = pageName;
		values[2] = languageHint;
		values[3] = lineNumbers;

		return values;

	}

	@Override
	public void encodeBegin(FacesContext context) throws IOException {

		super.encodeBegin(context);

		List<String> urls = new ArrayList<String>();

		String page = getPageName();

		if (StringUtil.isEmpty(page))
			return;

		if (page.endsWith(".xsp") && !page.startsWith("/")) {
			page = "/" + page;
		}

		urls.add(page);

		for (String string : urls) {

			InputStream is = null;

			try {

				is = context.getExternalContext().getResourceAsStream(string);

				StringBuilder sb = new StringBuilder();

				while (is.available() > 0) {
					sb.append((char) is.read());
				}

				String html = HtmlUtil.toHTMLContentString(sb.toString(), true);
				context.getResponseWriter().write(html);

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

		}

	}

}
