"use strict"; // Turns bad habits into errors :)

dojo.provide("extlib.dijit.MasterCheckbox");

dojo.require("dijit._WidgetBase");
dojo.require("dojo.io.script");
dojo.require("dojo.NodeList-manipulate");
dojo.require("dojo.NodeList-traverse");
dojo.require("dojo.dom-class");
dojo.require("dojo.dom-style");

dojo.declare('extlib.dijit.MasterCheckbox', dijit._WidgetBase, {

	constructor : function(args) {

		// Merge the supplied arguments into this dijit object
		dojo.safeMixin(this, args);

		var context = this;

		// Install the On Click Toggle
		XSP.attachClientFunction(this.id + "_MASTER", 'onclick', function() {
			context.toggle();
		});

		var cbs = this.checkboxes();

		dojo.forEach(cbs, function(cb) {
			
			var cbid = dojo.getAttr(cb,'id');
			var elem = cb;

			XSP.attachClientFunction(cbid, 'onclick', function(event) {
				event.cancelBubble = true;
			});

			XSP.attachClientFunction(cbid, 'onchange', function(event) {

				context.updateMaster();
				context.updateParentStyle(elem);


			});

			context.updateParentStyle(elem);
			
			if (context.parentSelectorClick) {
				
				var parentClick = dojo.query(elem).closest(context.parentSelectorClick);
				
				dojo.setStyle(parentClick[0], 'cursor', 'pointer');

				parentClick.onclick(function() {

					var ischecked = dojo.getAttr(elem, 'checked');
					
					if (ischecked) {
						dojo.setAttr(elem, 'checked', false);
					} else {
						dojo.setAttr(elem, 'checked', true);
					}

					context.updateParentStyle(elem);
					context.updateMaster();

				});
								
			}

		});

		context.updateMaster();

	},

	// elem = Checkbox Element
	updateParentStyle : function(elem) {

		if (this.parentSelectorStyleClass) {

			var parent = dojo.query(elem).closest(this.parentSelectorStyleClass)[0];
			
			var checked = dojo.getAttr(elem, 'checked');
			
			console.log(this.parentStyleClassChecked);
			
			if (checked) {
				
				if (this.parentStyleClassChecked) {
					dojo.addClass(parent, this.parentStyleClassChecked);
				}

				if (this.parentStyleClassUnchecked) {
					dojo.removeClass(parent, this.parentStyleClassUnchecked);				
				}
				
			} else {
				dojo.removeClass(parent, this.parentStyleClassChecked);
				dojo.addClass(parent, this.parentStyleClassUnchecked);
			}
						
		}
		

	},

	updateMaster : function() {

		var cb = dojo.byId(this.masterId);
				
		var cbs = this.checkboxes();

		var somethingchecked = false;
		var somethingunchecked = false;

		dojo.forEach(cbs, function(cb) {
			
			var cbchecked = dojo.getAttr(cb, 'checked');
			
			if (cbchecked) {
				somethingchecked = true;
			} else {
				somethingunchecked = true;
			}
		});

		if (somethingunchecked && somethingchecked) {
			
			dojo.setAttr(cb,'checked', false);
			dojo.setAttr(cb,'indeterminate', true);
			
		} else if (somethingchecked) {
			
			dojo.setAttr(cb,'indeterminate', false);
			dojo.setAttr(cb,'checked', true);
			
		} else {

			dojo.setAttr('indeterminate', false);
			dojo.setAttr('checked', false);

		}

	},

	toggle : function() {

		var cbs = this.checkboxes();

		var somethingchecked = false;
		var somethingunchecked = false;

		dojo.forEach(cbs, function(cb) {
			
			var cbchecked = dojo.getAttr(cb, 'checked');
			
			if (cbchecked) {
				somethingchecked = true;
			} else {
				somethingunchecked = true;
			}
			
		});

		if (somethingunchecked) {
			
			dojo.forEach(cbs, function(cb){
				dojo.setAttr(cb,'checked',true);	
			});
			
			
		} else if (somethingchecked) {

			dojo.forEach(cbs, function(cb){
				dojo.setAttr(cb,'checked',false);
			});
			
		}

		var context = this;

		dojo.forEach(cbs, function(cb) {
			context.updateParentStyle(cb);
		});

		this.updateMaster();

	},

	checkboxes : function() {
		return dojo.query('[name="' + this.id + '"]');
	},

	selectAll : function() {
		
		var cbs = this.checkboxes();
		dojo.setAttr(cbs, 'checked', true);
		
	},

	deselectAll : function() {

		var cbs = this.checkboxes();
		dojo.setAttr(cbs, 'checked', false);

		
	},

});