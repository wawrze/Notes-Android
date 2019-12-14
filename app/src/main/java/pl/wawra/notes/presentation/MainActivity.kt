package pl.wawra.notes.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import kotlinx.android.synthetic.main.activity_main.*
import pl.wawra.notes.R
import pl.wawra.notes.base.Navigation
import pl.wawra.notes.base.ToolbarInteraction

class MainActivity : AppCompatActivity(), ToolbarInteraction, Navigation {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun getNavigationController() = findNavController(R.id.nav_host_fragment)

    override fun onSupportNavigateUp() = getNavigationController().navigateUp()

    override fun setTopBarTitle(title: String) {
        activity_main_top_bar_title.text = title
    }

    override fun setLeftButtonAction(action: () -> Any) {
        activity_main_top_bar_left_button.setOnClickListener { action.invoke() }
    }

    override fun setRightButtonAction(action: () -> Any) {
        activity_main_top_bar_right_button.setOnClickListener { action.invoke() }
    }

    override fun setLeftButtonIcon(res: Int) {
        activity_main_top_bar_left_button.setImageResource(res)
    }

    override fun setRightButtonIcon(res: Int) {
        activity_main_top_bar_right_button.setImageResource(res)
    }

}