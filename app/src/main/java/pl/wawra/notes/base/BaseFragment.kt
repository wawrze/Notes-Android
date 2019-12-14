package pl.wawra.notes.base

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.navigation.NavController

abstract class BaseFragment : Fragment() {

    private var toolbarInteraction: ToolbarInteraction? = null
    protected var navigate: NavController? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        navigate = (context as? Navigation)?.getNavigationController()
        toolbarInteraction = activity as ToolbarInteraction
    }

    override fun onDetach() {
        super.onDetach()
        navigate = null
        toolbarInteraction = null
    }

    protected fun setUpTopBar(
        title: String,
        leftButtonAction: () -> Unit,
        rightButtonAction: () -> Unit,
        leftIconRes: Int,
        rightIconRes: Int
    ) {
        toolbarInteraction?.setTopBarTitle(title)
        toolbarInteraction?.setLeftButtonAction(leftButtonAction)
        toolbarInteraction?.setRightButtonAction(rightButtonAction)
        toolbarInteraction?.setLeftButtonIcon(leftIconRes)
        toolbarInteraction?.setRightButtonIcon(rightIconRes)
    }

}