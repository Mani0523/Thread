package com.example.thread.screens

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.thread.R
import com.example.thread.navigation.Routes
import com.example.thread.utils.SharedPref
import com.example.thread.viewmodel.AddThreadViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AddThreads(navHostController: NavHostController) {
    val threadViewModel: AddThreadViewModel = viewModel()
    val isPosted by threadViewModel.isPosted.observeAsState(false)
    val context = LocalContext.current

    var thread by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val permissionToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        android.Manifest.permission.READ_MEDIA_IMAGES
    } else {
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri = uri
    }

    val permissionLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            launcher.launch("image/*")
        } else {
            // Handle permission denied case
        }
    }

    LaunchedEffect(isPosted) {
        if (isPosted == true) {
            thread = ""
            imageUri = null
            Toast.makeText(context, "Thread Added", Toast.LENGTH_SHORT).show()

            navHostController.navigate(Routes.Home.routes) {
                popUpTo(Routes.AddThreads.routes) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val (crossPic, title, logo, userName, editText, replyText, button, imageBox) = createRefs()

        Text(
            text = "Add Thread",
            style = TextStyle(fontWeight = FontWeight.ExtraBold, fontSize = 24.sp),
            modifier = Modifier.constrainAs(title) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
            }
        )

        Image(
            painter = rememberAsyncImagePainter(model = SharedPref.getImage(context)),
            contentDescription = "Profile Picture",
            modifier = Modifier
                .constrainAs(logo) {
                    top.linkTo(title.bottom, margin = 16.dp)
                    start.linkTo(parent.start)
                }
                .size(36.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Text(
            text = SharedPref.getUserName(context),
            style = TextStyle(fontSize = 20.sp),
            modifier = Modifier.constrainAs(userName) {
                top.linkTo(logo.top)
                start.linkTo(logo.end, margin = 12.dp)
                bottom.linkTo(logo.bottom)
            }
        )

        BasicTextFieldWithHint(
            hint = "Start a thread...",
            value = thread,
            onValueChange = { thread = it },
            modifier = Modifier
                .constrainAs(editText) {
                    top.linkTo(userName.bottom, margin = 16.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .fillMaxWidth()
        )

        if (imageUri == null) {
            Image(
                painter = painterResource(id = R.drawable.person),
                contentDescription = "Add Image",
                modifier = Modifier
                    .constrainAs(crossPic) {
                        top.linkTo(editText.bottom, margin = 16.dp)
                        start.linkTo(editText.start)
                    }
                    .clickable {
                        val isGranted = ContextCompat.checkSelfPermission(
                            context,
                            permissionToRequest
                        ) == PackageManager.PERMISSION_GRANTED

                        if (isGranted) {
                            launcher.launch("image/*")
                        } else {
                            permissionLauncher.launch(permissionToRequest)
                        }
                    }
            )
        } else {
            Box(
                modifier = Modifier
                    .background(Color.Gray)
                    .padding(12.dp)
                    .constrainAs(imageBox) {
                        top.linkTo(editText.bottom, margin = 16.dp)
                        start.linkTo(editText.start)
                        end.linkTo(parent.end)
                    }
                    .height(200.dp)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = imageUri),
                    contentDescription = "Selected Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    contentScale = ContentScale.Crop
                )

                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove Image",
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .clickable { imageUri = null }
                )
            }
        }

        Text(
            text = "Anyone can reply",
            style = TextStyle(fontSize = 20.sp),
            modifier = Modifier.constrainAs(replyText) {
                start.linkTo(parent.start, margin = 12.dp)
                bottom.linkTo(parent.bottom, margin = 12.dp)
            }
        )

        TextButton(onClick = {
            if (imageUri == null) {
                threadViewModel.saveData(thread, FirebaseAuth.getInstance().currentUser!!.uid, "")
            } else {
                threadViewModel.saveImage(thread, FirebaseAuth.getInstance().currentUser!!.uid, imageUri!!)
            }
        }, modifier = Modifier.constrainAs(button) {
            end.linkTo(parent.end, margin = 12.dp)
            bottom.linkTo(parent.bottom, margin = 12.dp)
        }) {
            Text(text = "Post", style = TextStyle(fontSize = 20.sp))
        }
    }
}

@Composable
fun BasicTextFieldWithHint(
    hint: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        if (value.isEmpty()) {
            Text(text = hint, color = Color.Gray)
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle.Default.copy(color = Color.Black),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AddPostView() {
    // You can uncomment and use this for previewing
    // AddThreads(navHostController = rememberNavController())
}
