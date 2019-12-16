package pl.wawra.notes.base

interface ToolbarInteraction {

    fun setTopBarTitle(title: String)
    fun setLeftButtonAction(action: (() -> Any)?)
    fun setRightButtonAction(action: () -> Any)
    fun setLeftButtonIcon(res: Int?)
    fun setRightButtonIcon(res: Int)

}