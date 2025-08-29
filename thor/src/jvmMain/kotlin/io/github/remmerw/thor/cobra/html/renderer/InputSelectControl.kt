package io.github.remmerw.thor.cobra.html.renderer

import io.github.remmerw.thor.cobra.html.domimpl.HTMLBaseInputElement
import io.github.remmerw.thor.cobra.html.domimpl.HTMLSelectElementImpl
import io.github.remmerw.thor.cobra.util.gui.WrapperLayout
import org.w3c.dom.html.HTMLOptionElement
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.event.ItemEvent
import java.awt.event.ItemListener
import java.util.LinkedList
import javax.swing.DefaultListModel
import javax.swing.JComboBox
import javax.swing.JList
import javax.swing.JScrollPane
import javax.swing.ListSelectionModel
import javax.swing.event.ListSelectionEvent
import javax.swing.event.ListSelectionListener

internal class InputSelectControl(modelNode: HTMLBaseInputElement?) : BaseInputControl(modelNode) {
    private val comboBox: JComboBox<OptionItem?>
    private val list: JList<OptionItem?>
    private val listModel: DefaultListModel<OptionItem?>
    private var inSelectionEvent = false
    private var state: Int = STATE_NONE
    private var suspendSelections = false
    override var selectedIndex: Int = -1
        set(value) {
            field = value
            val prevSuspend = this.suspendSelections
            this.suspendSelections = true
            // Note that neither IE nor FireFox generate selection
            // events when the selection is changed programmatically.
            try {
                if (!this.inSelectionEvent) {
                    if (this.state == STATE_COMBO) {
                        val comboBox = this.comboBox
                        if (comboBox.selectedIndex != value) {
                            // This check is done to avoid an infinite recursion
                            // on ItemListener.
                            val size = comboBox.itemCount
                            if (value < size) {
                                comboBox.setSelectedIndex(value)
                            }
                        }
                    } else {
                        val list = this.list
                        val selectedIndices = list.selectedIndices
                        if ((selectedIndices == null) || (selectedIndices.size != 1) || (selectedIndices[0] != value)) {
                            // This check is done to avoid an infinite recursion
                            // on ItemListener.
                            val size = this.listModel.size
                            if (value < size) {
                                list.setSelectedIndex(value)
                            }
                        }
                    }
                }
            } finally {
                this.suspendSelections = prevSuspend
            }
        }

    init {
        this.layout = WrapperLayout.instance
        val comboBox: JComboBox<OptionItem?> = JComboBox<OptionItem?>()
        comboBox.addItemListener(object : ItemListener {
            override fun itemStateChanged(e: ItemEvent) {
                val item: OptionItem? = e.getItem() as OptionItem?
                if (item != null) {
                    when (e.getStateChange()) {
                        ItemEvent.SELECTED -> if (!suspendSelections) {
                            // In this case it's better to change the
                            // selected index. We don't want multiple selections.
                            inSelectionEvent = true
                            try {
                                val selectedIndex = comboBox.selectedIndex
                                val selectElement = modelNode as HTMLSelectElementImpl
                                selectElement.setSelectedIndex(selectedIndex)
                            } finally {
                                inSelectionEvent = false
                            }
                            HtmlController.Companion.instance.onChange(modelNode)
                        }

                        ItemEvent.DESELECTED -> {}
                    }
                }
            }
        })
        val listModel: DefaultListModel<OptionItem?> = DefaultListModel<OptionItem?>()
        val list: JList<OptionItem?> = JList<OptionItem?>(listModel)
        this.listModel = listModel
        list.selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
        list.addListSelectionListener(object : ListSelectionListener {
            override fun valueChanged(e: ListSelectionEvent) {
                if (!e.valueIsAdjusting && !suspendSelections) {
                    var changed = false
                    inSelectionEvent = true
                    try {
                        val modelSize = listModel.size
                        for (i in 0..<modelSize) {
                            val item = listModel.get(i)
                            if (item != null) {
                                val oldIsSelected = item.isSelected
                                val newIsSelected = list.isSelectedIndex(i)
                                if (oldIsSelected != newIsSelected) {
                                    changed = true
                                    item.isSelected = newIsSelected
                                }
                            }
                        }
                    } finally {
                        inSelectionEvent = false
                    }
                    if (changed) {
                        HtmlController.Companion.instance.onChange(modelNode)
                    }
                }
            }
        })

        // Note: Value attribute cannot be set in reset() method.
        // Otherwise, layout revalidation causes typed values to
        // be lost (including revalidation due to hover.)
        this.comboBox = comboBox
        this.list = list
        this.resetItemList()
    }

    private fun resetItemList() {
        val selectElement = this.controlElement as HTMLSelectElementImpl
        val isMultiple = selectElement.getMultiple()
        if (isMultiple && (this.state != STATE_LIST)) {
            this.state = STATE_LIST
            this.removeAll()
            val scrollPane = JScrollPane(this.list)
            this.add(scrollPane)
        } else if (!isMultiple && (this.state != STATE_COMBO)) {
            this.state = STATE_COMBO
            this.removeAll()
            this.add(this.comboBox)
        }
        this.suspendSelections = true
        try {
            val optionElements = selectElement.options
            if (this.state == STATE_COMBO) {
                val comboBox = this.comboBox
                // First determine current selected option
                var priorSelectedOption: HTMLOptionElement? = null
                val priorIndex = selectElement.getSelectedIndex()
                if (priorIndex != -1) {
                    val numOptions = optionElements.length
                    for (index in 0..<numOptions) {
                        val option = optionElements.item(index) as HTMLOptionElement?
                        if (index == priorIndex) {
                            priorSelectedOption = option
                        }
                    }
                }
                comboBox.removeAllItems()
                var defaultItem: OptionItem? = null
                var selectedItem: OptionItem? = null
                var firstItem: OptionItem? = null
                val numOptions = optionElements.length
                for (index in 0..<numOptions) {
                    val option = optionElements.item(index) as HTMLOptionElement?
                    if (option != null) {
                        val item = OptionItem(option)
                        if (firstItem == null) {
                            firstItem = item
                            comboBox.addItem(item)
                            // Undo automatic selection that occurs
                            // when adding the first item.
                            // This might set the deferred index as well.
                            selectElement.setSelectedIndex(-1)
                            if (priorSelectedOption != null) {
                                priorSelectedOption.selected = true
                            }
                        } else {
                            comboBox.addItem(item)
                        }
                        if (option.selected) {
                            selectedItem = item
                        }
                        if (option.defaultSelected) {
                            defaultItem = item
                        }
                    }
                }
                if (selectedItem != null) {
                    comboBox.selectedItem = selectedItem
                } else if (defaultItem != null) {
                    comboBox.selectedItem = defaultItem
                } else if (firstItem != null) {
                    comboBox.selectedItem = firstItem
                }
            } else {
                val list = this.list
                var defaultSelectedIndexes: MutableCollection<Int>? = null
                var selectedIndexes: MutableCollection<Int>? = null
                var firstItem: OptionItem? = null
                val listModel = this.listModel
                listModel.clear()
                val numOptions = optionElements.length
                for (index in 0..<numOptions) {
                    val option = optionElements.item(index) as HTMLOptionElement
                    val item = OptionItem(option)
                    if (firstItem == null) {
                        firstItem = item
                        listModel.addElement(item)
                        // Do not select first item automatically.
                        list.selectedIndex = -1
                    } else {
                        listModel.addElement(item)
                    }
                    if (option.selected) {
                        if (selectedIndexes == null) {
                            selectedIndexes = LinkedList<Int>()
                        }
                        selectedIndexes.add(index)
                    }
                    if (option.defaultSelected) {
                        if (defaultSelectedIndexes == null) {
                            defaultSelectedIndexes = LinkedList<Int>()
                        }
                        defaultSelectedIndexes.add(index)
                    }
                }
                if ((selectedIndexes != null) && (selectedIndexes.size != 0)) {
                    val sii: MutableIterator<Int> = selectedIndexes.iterator()
                    while (sii.hasNext()) {
                        val si = sii.next()
                        list.addSelectionInterval(si, si)
                    }
                } else if ((defaultSelectedIndexes != null) && (defaultSelectedIndexes.size != 0)) {
                    val sii: MutableIterator<Int> = defaultSelectedIndexes.iterator()
                    while (sii.hasNext()) {
                        val si = sii.next()
                        list.addSelectionInterval(si, si)
                    }
                }
            }
        } finally {
            this.suspendSelections = false
        }
    }

    override fun reset(availWidth: Int, availHeight: Int) {
        super.reset(availWidth, availHeight)
        // Need to do this here in case element was incomplete
        // when first rendered.
        this.resetItemList()
    }

    override var preferredSize: Dimension?
        get() = TODO("Not yet implemented")
        set(value) {}
    override val backgroundColor: Color?
        get() = TODO("Not yet implemented")
    override var component: Component?
        get() = TODO("Not yet implemented")
        set(value) {}
    override var name: String?
        get() = TODO("Not yet implemented")
        set(value) {}

    override var value: String? = null
        get() {
            if (this.state == STATE_COMBO) {
                val item: OptionItem? = this.comboBox.selectedItem as OptionItem?
                return if (item == null) null else item.value
            } else {
                val item = this.list.getSelectedValue()
                return if (item == null) null else item.value
            }
        }

    override var visibleSize: Int
        get() = this.comboBox.getMaximumRowCount()
        set(value) {
            this.comboBox.setMaximumRowCount(value)
        }

    override fun resetInput() {
        this.list.selectedIndex = -1
        this.comboBox.selectedIndex = -1
    }

    override val values: Array<String?>?
        get() {
            if (this.state == STATE_COMBO) {
                val item: OptionItem? = this.comboBox.selectedItem as OptionItem?
                return if (item == null) null else arrayOf<String?>(item.value)
            } else {
                val values = this.list.selectedValues
                if (values == null) {
                    return null
                }
                val al = ArrayList<String?>()
                for (value2 in values) {
                    val item: OptionItem = value2 as OptionItem
                    al.add(item.value)
                }
                return al.toTypedArray<String?>()
            }
        }

    private class OptionItem(private val option: HTMLOptionElement) {
        private val caption: String?

        init {
            val label = option.label
            if (label == null) {
                this.caption = option.text
            } else {
                this.caption = label
            }
        }

        var isSelected: Boolean
            get() = this.option.selected
            set(value) {
                this.option.setSelected(value)
            }

        override fun toString(): String {
            return this.caption!!
        }

        val value: String?
            get() {
                var value = this.option.value
                if (value == null) {
                    value = this.option.text
                }
                return value
            }
    }

    companion object {
        private const val serialVersionUID = 286101283473109265L
        private const val STATE_NONE = 0
        private const val STATE_COMBO = 1
        private const val STATE_LIST = 2
    }
}
