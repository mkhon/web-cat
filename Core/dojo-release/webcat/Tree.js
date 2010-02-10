/*==========================================================================*\
 |  $Id: Tree.js,v 1.5 2010/02/10 18:17:31 aallowat Exp $
 |*-------------------------------------------------------------------------*|
 |  Copyright (C) 2006-2008 Virginia Tech
 |
 |  This file is part of Web-CAT.
 |
 |  Web-CAT is free software; you can redistribute it and/or modify
 |  it under the terms of the GNU Affero General Public License as published
 |  by the Free Software Foundation; either version 3 of the License, or
 |  (at your option) any later version.
 |
 |  Web-CAT is distributed in the hope that it will be useful,
 |  but WITHOUT ANY WARRANTY; without even the implied warranty of
 |  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 |  GNU General Public License for more details.
 |
 |  You should have received a copy of the GNU Affero General Public License
 |  along with Web-CAT; if not, see <http://www.gnu.org/licenses/>.
\*==========================================================================*/

dojo.provide("webcat.Tree");

dojo.require("dojo.cache");
dojo.require("dijit.Tree");

// ------------------------------------------------------------------------
/**
 * A Dojo tree model (for dijit.Tree) that obtains its data from a JSONBridge.
 *
 * @author Tony Allevato
 * @version $Id: Tree.js,v 1.5 2010/02/10 18:17:31 aallowat Exp $
 */
dojo.declare("webcat.JSONBridgeTreeModel", null,
{
    //~ Properties ............................................................

    root: null,
    rootLabel: "ROOT",
    proxy: null,

    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new instance of AjaxProxyTreeModel.
     *
     * @param args an Object containing properties to be assigned to the new
     *     instance.
     *     proxy: the JavaScript name of the AjaxProxy to use as the delegate
     *         that will be called to populate the tree
     */
    constructor: function(/* Object */ args)
    {
        dojo.mixin(this, args);

        this.root = {
            root: true,
            itemId: "/",
            label: this.rootLabel,
        };
    },


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Calls a function passing it the root item of the tree.
     *
     * @param onItem the function to call with the root item of the tree
     * @param onError an error handler to call if there was an error getting
     *     the root
     */
    getRoot: function(onItem, onError)
    {
        onItem(this.root);
    },


    // ----------------------------------------------------------
    /**
     * Gets a value indicating whether the specified item in the tree might
     * have children.
     *
     * @param item the parent item
     * @return true if the item has children; otherwise false.
     */
    mayHaveChildren: function(/* item */ item)
    {
        if (item === this.root)
        {
            return true;
        }
        else
        {
            return item.hasChildren;
        }
    },


    // ----------------------------------------------------------
    /**
     * Gets the children of the specified item in the tree and calls a callback
     * function with that array of children.
     *
     * @param parentItem the item whose children should be obtained
     * @param callback the function to call with the array of children
     * @param onError an error handler if there were problems accessing the
     *     children
     */
    getChildren: function(/* item */ parentItem,
                          /* function(items) */ onComplete,
                          /* function */ onError)
    {
        if (parentItem === this.root)
        {
            if (this.root.children)
            {
                onComplete(this.root.children);
            }
            else
            {
                var self = this;

                this.proxy._childrenOfItemWithId(function(result)
                {
                    self.root.children = result;
                    onComplete(result);
                },
                null);
            }
        }
        else
        {
            this.proxy._childrenOfItemWithId(function(result)
            {
                onComplete(result);
            },
            parentItem.itemId);
        }
    },


    // ----------------------------------------------------------
    /**
     * Gets a unique identifier for this tree item.
     *
     * @param item the tree item
     * @return the unique identifier for the tree item
     */
    getIdentity: function(/* item */ item)
    {
        if (item === this.root)
        {
            return this.root.itemId;
        }
        else
        {
            return item.itemId;
        }
    },


    // ----------------------------------------------------------
    /**
     * Gets a label to display for this tree item.
     *
     * @param item the tree item
     * @return the label to display for the tree item
     */
    getLabel: function(/* item */ item)
    {
        if (item === this.root)
        {
            return this.root.label;
        }
        else
        {
            return item.label;
        }
    }
});


// ------------------------------------------------------------------------
/**
 * A node in a DecoratedTree.
 */
dojo.declare("webcat.DecoratedTreeNode", dijit._TreeNode,
{
    //~ Properties ............................................................

    widgetsInTemplate: true,

    templateString: dojo.cache("webcat", "templates/DecoratedTreeNode.html"),

    id: null,

    // Copied from dijit._TreeNode to change labelNode from innerText to
    // innerHTML.
    attributeMap: dojo.delegate(dijit._Widget.prototype.attributeMap, {
        label: {node: "labelNode", type: "innerHTML"},
        tooltip: {node: "rowNode", type: "attribute", attribute: "title"}
    }),

    //~ Methods ...............................................................

    // ----------------------------------------------------------
    postCreate: function()
    {
        this.inherited(arguments);

        this.tree.decorateNode(this);
    },


    // ----------------------------------------------------------
    setLabelNode: function(label)
    {
        this.labelNode.innerHTML = label;
    },


    // ----------------------------------------------------------
    setChildItems: function(/* Object[] */ items)
    {
        // SYNCHRONIZED WITH DOJO 1.4.1.

        // summary:
        //		Sets the child items of this node, removing/adding nodes
        //		from current children to match specified items[] array.
        //		Also, if this.persist == true, expands any children that were previously
        // 		opened.
        // returns:
        //		Deferred object that fires after all previously opened children
        //		have been expanded again (or fires instantly if there are no such children).

        var tree = this.tree,
            model = tree.model,
            defs = [];	// list of deferreds that need to fire before I am complete


        // Orphan all my existing children.
        // If items contains some of the same items as before then we will reattach them.
        // Don't call this.removeChild() because that will collapse the tree etc.
        this.getChildren().forEach(function(child){
            dijit._Container.prototype.removeChild.call(this, child);
        }, this);

        this.state = "LOADED";

        if(items && items.length > 0){
            this.isExpandable = true;

            // Create _TreeNode widget for each specified tree node, unless one already
            // exists and isn't being used (presumably it's from a DnD move and was recently
            // released
            dojo.forEach(items, function(item){
                var id = model.getIdentity(item),
                    existingNodes = tree._itemNodesMap[id],
                    node;
                if(existingNodes){
                    for(var i=0;i<existingNodes.length;i++){
                        if(existingNodes[i] && !existingNodes[i].getParent()){
                            node = existingNodes[i];
                            node.attr('indent', this.indent+1);
                            break;
                        }
                    }
                }
                if(!node){
                    node = this.tree._createTreeNode({
// BEGIN WEBCAT CHANGES
                            id: id,
// END WEBCAT CHANGES
                            item: item,
                            tree: tree,
                            isExpandable: model.mayHaveChildren(item),
                            label: tree.getLabel(item),
                            tooltip: tree.getTooltip(item),
                            indent: this.indent + 1
                        });
                    if(existingNodes){
                        existingNodes.push(node);
                    }else{
                        tree._itemNodesMap[id] = [node];
                    }
                }
                this.addChild(node);

                // If node was previously opened then open it again now (this may trigger
                // more data store accesses, recursively)
                if(this.tree.autoExpand || this.tree._state(item)){
                    defs.push(tree._expandNode(node));
                }
            }, this);

            // note that updateLayout() needs to be called on each child after
            // _all_ the children exist
            dojo.forEach(this.getChildren(), function(child, idx){
                child._updateLayout();
            });
        }else{
            this.isExpandable=false;
        }

        if(this._setExpando){
            // change expando to/from dot or + icon, as appropriate
            this._setExpando(false);
        }

        // On initial tree show, make the selected TreeNode as either the root node of the tree,
        // or the first child, if the root node is hidden
        if(this == tree.rootNode){
            var fc = this.tree.showRoot ? this : this.getChildren()[0];
            if(fc){
                fc.setSelected(true);
                tree.lastFocused = fc;
            }else{
                // fallback: no nodes in tree so focus on Tree <div> itself
                tree.domNode.setAttribute("tabIndex", "0");
            }
        }

        return new dojo.DeferredList(defs);	// dojo.Deferred
    }
});


// ------------------------------------------------------------------------
/**
 * A tree that allows nodes to be "decorated", by handling the "decorateNode"
 * event to attach extra widgets and data to nodes as they are created.
 */
dojo.declare("webcat.DecoratedTree", dijit.Tree,
{
    //~ Properties ............................................................

    formFieldName: "",
    decorationOptions: {},


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    _createTreeNode: function( /* Object */ args)
    {
        return new webcat.DecoratedTreeNode(args);
    },


    // ----------------------------------------------------------
    decorateNode: function( /* DecoratedNode */ node)
    {
    },


    // ----------------------------------------------------------
    getIconClass: function(item)
    {
        if (item && item.iconClass)
            return item.iconClass;
        else
            return this.inherited(arguments);
    }
});


//~ Global Functions ..........................................................

webcat.tree = {
    // --------------------------------------------------------------
    /**
     * The handler that should be called when a DecoratedTree node is clicked. This
     * function performs the preOnClick -> server onClick -> postOnClick behavior
     * described in the Tree component documenation.
     */
    onNodeClick: function(/* DecoratedTree */ tree,
                          /* object */ item,
                          /* DecoratedTreeNode */ node,
                          /* JSONBridge */ proxyReference)
    {
        var continueHandling = true;

        if (tree.preOnClick)
            continueHandling = tree.preOnClick(item, node);

        if (continueHandling)
        {
            var serverResult = proxyReference.handleOnClick(item.itemId);

            if (tree.postOnClick)
                tree.postOnClick(item, node, serverResult);
        }
    },


    // --------------------------------------------------------------
    /**
     * For a CheckTree with independentChecks set to false, this function fixes the
     * checked states of the ancestor nodes based on the checked states of their
     * children.
     */
    fixChecksUp: function(/* DecoratedTreeNode */ node,
                          /* string[] */ touchedItems)
    {
        if (node)
        {
            var allChecked = node.getChildren().every(function(child)
            {
                return child.isChecked();
            });

            if (allChecked != node.isChecked())
            {
                touchedItems.push(node.id);
                node.setChecked(allChecked);
            }

            webcat.tree.fixChecksUp(node.getParent(), touchedItems);
        }
    },


    // --------------------------------------------------------------
    /**
     * For a CheckTree with independentChecks set to false, this function fixes the
     * checked states of the child nodes based on the checked states of the parent.
     */
    fixChecksDown: function(/* DecoratedTreeNode */ node,
                            /* boolean */ newValue,
                            /* string[] */ touchedItems)
    {
        node.getChildren().forEach(function(child)
        {
            if (newValue != child.isChecked())
            {
                touchedItems.push(child.id);
                child.setChecked(newValue);
            }

            webcat.tree.fixChecksDown(child, newValue, touchedItems);
        });
    },


    // --------------------------------------------------------------
    /**
     * The event handler that is called when the checked state of a node in the
     * tree is changed by the user.
     */
    onCheckChanged: function(/* boolean */ newValue,
                             /* DecoratedTreeNode */ node,
                             /* DecoratedTree */ tree)
    {
        if (!tree._automaticCheckChange)
        {
            // Keep track of the nodes that were changed by this operation.
            var touchedItems = [ node.id ];
            var indep = tree.decorationOptions.independentChecks;

            if (indep != true && !tree._automaticCheckChange)
            {
                tree._automaticCheckChange = true;

                webcat.tree.fixChecksDown(node, newValue, touchedItems);
                webcat.tree.fixChecksUp(node.getParent(), touchedItems);

                tree._automaticCheckChange = false;
            }

            // Notify the server-side component that the check state has changed
            // so that it can stay synchronized.

            tree.checkTreeProxy._handleCheckChanged(function(result)
            {
                // Only pass the event along to an observer once the server-side
                // component has been successfully synched.

                if (tree.onCheckChanged)
                    tree.onCheckChanged(node.item, newValue, node);
            },
            touchedItems, newValue);
        }
    },


    // --------------------------------------------------------------
    /**
     * Handles the decorateNode event of a DecoratedTree for the CheckTree
     * component. The node is decorated by adding a checkbox widget before the
     * node's icon.
     */
    decorateCheckTreeNode: function(/* DecoratedTree */ tree,
                                    /* DecoratedTreeNode */ node,
                                    /* string */ formFieldName)
    {
        var checkboxArgs = {
            name: formFieldName,
            value: node.id,
            onClick: function(e) {
                webcat.tree.onCheckChanged(this.attr('checked'), node, tree)
            }
        };

        node.checkbox = new dijit.form.CheckBox(checkboxArgs, node.leftDecoration);
        node.checkbox.attr('checked', node.item.checked || false);

        node.isChecked = function()
        {
            return this.checkbox.attr('checked') || false;
        };

        node.setChecked = function(newValue)
        {
            this.checkbox.attr('checked', newValue);
        };
    }
}; // end webcat.tree
