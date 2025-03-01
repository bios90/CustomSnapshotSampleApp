package com.example.customsnapshotsampleapp.ui.screens.welcome

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.customsnapshotsampleapp.R
import com.example.customsnapshotsampleapp.ui.theme.ContentColorSecondary
import com.example.customsnapshotsampleapp.ui.theme.CustomSnapshotSampleAppTheme
import com.example.customsnapshotsampleapp.ui.theme.SurfaceDark
import com.example.roborazzisampleapp.ui.components.PrimaryButton
import com.example.roborazzisampleapp.ui.components.SecondaryButton

@Composable
fun WelcomeScreen(
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = SurfaceDark
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(140.dp),
                    painter = painterResource(R.drawable.ic_logo),
                    contentDescription = null,
                    tint = ContentColorSecondary
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                SecondaryButton(
                    modifier = Modifier
                        .padding(top = 86.dp)
                        .padding(horizontal = 36.dp)
                        .height(52.dp),
                    text = stringResource(R.string.sign_up),
                    onClick = {}
                )
                PrimaryButton(
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .height(52.dp)
                        .padding(horizontal = 36.dp),
                    text = stringResource(R.string.sign_in),
                    onClick = {}
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    CustomSnapshotSampleAppTheme {
        WelcomeScreen()
    }
}
