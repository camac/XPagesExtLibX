/*
 * © Copyright IBM Corp. 2010
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 */

package com.ibm.xsp.extlibx.resources;

import java.util.IdentityHashMap;

import javax.faces.context.FacesContext;

import com.ibm.xsp.component.UIViewRootEx;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.resource.DojoModuleResource;
import com.ibm.xsp.resource.Resource;
import com.ibm.xsp.resource.ScriptResource;
import com.ibm.xsp.resource.StyleSheetResource;

public class ExtLibXResources {

    public static void addEncodeResources(FacesContext context, Resource[] resources) {
        UIViewRootEx rootEx = (UIViewRootEx)context.getViewRoot();
        addEncodeResources(rootEx,resources);
    }
    
    public static void addEncodeResources(UIViewRootEx rootEx, Resource[] resources) {
        if(resources!=null) {
            for(int i=0; i<resources.length; i++) {
                addEncodeResource(rootEx,resources[i]);
            }
        }
    }

    public static void addEncodeResource(FacesContext context, Resource resource) {
        UIViewRootEx rootEx = (UIViewRootEx)context.getViewRoot();
        addEncodeResource(rootEx,resource);
    }
    
    @SuppressWarnings("unchecked") // $NON-NLS-1$
    public static void addEncodeResource(UIViewRootEx rootEx, Resource resource) {
        if(ExtLibUtil.isXPages852()) {
            // The XPages runtime add all the resources and does a check when it starts to
            // generate all the resources at the very end.
            // For performance reasons, and until the XPages runtime optimizes this, we ensure
            // that the same resource (the exact same object - identity comparison) is not
            // added multiple times.
            // Already optimized in post 852
            IdentityHashMap<Resource, Boolean> m = (IdentityHashMap<Resource, Boolean>)rootEx.getEncodeProperty("extlib.EncodeResource"); // $NON-NLS-1$
            if(m==null) {
                m = new IdentityHashMap<Resource, Boolean>();
            } else {
                if(m.containsKey(resource)) {
                    return;
                }
            }
            m.put(resource, Boolean.TRUE);
        }
        rootEx.addEncodeResource(resource);
    }
   
    public static final DojoModuleResource dijitMasterCheckbox = new DojoModuleResource("dijit.form.MasterCheckbox");
    
    public static final DojoModuleResource extlibMasterCheckbox = new DojoModuleResource("extlib.dijit.MasterCheckbox"); // $NON-NLS-1$
    
    public static final ScriptResource extlibPrettify = new ScriptResource("/.ibmxspres/.extlib/google-code-prettify/prettify.js", true);
    public static final StyleSheetResource extlibPrettifyCss = new StyleSheetResource("/.ibmxspres/.extlib/google-code-prettify/prettify.css");
    
}