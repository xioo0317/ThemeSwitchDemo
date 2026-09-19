package com.demo.themeswitch.ui.component

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.demo.themeswitch.ui.theme.LocalShowSwitchIcon

/**
 * KSU ExpressiveSwitch 同款：thumb 上带勾/叉小图标，
 * 是否显示图标由「切换开关图标」偏好（LocalShowSwitchIcon）控制。
 */
@Composable
fun ExpressiveSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val showIcon = LocalShowSwitchIcon.current
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        thumbContent = if (showIcon) {
            {
                Icon(
                    imageVector = if (checked) Icons.Filled.Check else Icons.Rounded.Close,
                    contentDescription = null,
                    modifier = Modifier.size(SwitchDefaults.IconSize),
                )
            }
        } else {
            null
        },
    )
}
