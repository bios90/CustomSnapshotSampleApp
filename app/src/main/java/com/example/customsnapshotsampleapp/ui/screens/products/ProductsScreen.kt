package com.example.roborazzisampleapp.ui.screens.products

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.customsnapshotsampleapp.R
import com.example.customsnapshotsampleapp.ui.theme.ContentColorPrimary
import com.example.customsnapshotsampleapp.ui.theme.CustomSnapshotSampleAppTheme
import com.example.customsnapshotsampleapp.ui.theme.PurpleDark
import com.example.customsnapshotsampleapp.ui.theme.SurfaceDark
import com.example.customsnapshotsampleapp.ui.theme.Typography
import com.example.roborazzisampleapp.ui.components.SecondaryButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    items: List<Product>,
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
                        text = stringResource(R.string.products),
                        color = SurfaceDark
                    )
                }
            )
        },
        bottomBar = {}
    ) { paddingValues ->
        Column(modifier = modifier.padding(paddingValues)) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(12.dp)
            ) {
                items(items) { item ->
                    ItemProduct(
                        productVm = item,
                        onClick = {}
                    )
                }
            }
        }
    }
}

@Composable
private fun ItemProduct(
    productVm: Product,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clip(RoundedCornerShape(24.dp))
            .background(ContentColorPrimary)
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Text(
            modifier = Modifier.align(Alignment.Start),
            text = productVm.name,
            style = Typography.titleSmall,
            maxLines = 2,
            fontWeight = FontWeight.Bold,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            modifier = Modifier
                .align(Alignment.Start)
                .padding(top = 12.dp),
            text = productVm.description,
            style = Typography.bodyMedium,
            maxLines = 8,
            overflow = TextOverflow.Ellipsis
        )
        SecondaryButton(
            modifier = Modifier.padding(top = 16.dp),
            text = stringResource(R.string.add),
            onClick = {}
        )
    }
}

@Preview
@Composable
private fun Preview() {
    CustomSnapshotSampleAppTheme {
        ProductsScreen(
            items = MockProducts,
        )
    }
}
