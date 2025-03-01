package com.example.customsnapshotsampleapp.ui.screens.product_details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.customsnapshotsampleapp.R
import com.example.customsnapshotsampleapp.ui.theme.ContentColorPrimary
import com.example.customsnapshotsampleapp.ui.theme.CustomSnapshotSampleAppTheme
import com.example.customsnapshotsampleapp.ui.theme.PurpleDark
import com.example.customsnapshotsampleapp.ui.theme.SurfaceDark
import com.example.customsnapshotsampleapp.ui.theme.Typography
import com.example.roborazzisampleapp.ui.components.PrimaryButton
import com.example.roborazzisampleapp.ui.components.SecondaryButton
import com.example.roborazzisampleapp.ui.screens.products.MockProducts
import com.example.roborazzisampleapp.ui.screens.products.Product

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ProductDetailsScreen(
    product: Product,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        containerColor = SurfaceDark,
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = PurpleDark,
                ),
                title = {
                    Text(
                        text = stringResource(R.string.product),
                        color = SurfaceDark
                    )
                }
            )
        },
        bottomBar = {
            BottomBar()
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = product.name,
                style = Typography.titleMedium,
                color = ContentColorPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = product.description,
                color = ContentColorPrimary,
                style = Typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun BottomBar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SecondaryButton(
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            text = stringResource(R.string.to_basket),
            onClick = {}
        )
        PrimaryButton(
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            text = stringResource(R.string.buy),
            onClick = {}
        )
    }
}

@Preview
@Composable
private fun Preview() {
    CustomSnapshotSampleAppTheme {
        ProductDetailsScreen(
            product = MockProducts.get(5)
        )
    }
}
