package permissions.dispatcher.processor.data

object NewV13 {

    public val ACTUAL: List<String> = listOf(
            "package permissions.dispatcher.test;",
            "import android.Manifest;",
            "import android.app.Fragment;",
            "import permissions.dispatcher.Needs;",
            "import permissions.dispatcher.OnRationale;",
            "import permissions.dispatcher.RuntimePermissions;",
            "@RuntimePermissions",
            "public class MyFragment extends Fragment {",
            "   @Needs(Manifest.permission.CAMERA)",
            "   void showCamera() {",
            "   }",
            "   @OnRationale(Manifest.permission.CAMERA)",
            "   void onCameraRationale() {",
            "   }",
            "}"
    )

    public val EXPECT: List<String> = listOf(
            "// This file was generated by PermissionsDispatcher. Do not modify!",
            "package permissions.dispatcher.test;",
            "import android.support.v4.app.ActivityCompat;",
            "import java.lang.String;",
            "import permissions.dispatcher.PermissionUtils;",
            "final class MyActivityPermissionsDispatcher {",
            "   private static final int REQUEST_SHOWCAMERA = 0;",
            "   private static final String[] PERMISSION_SHOWCAMERA = new String[] {\"android.permission.CAMERA\"};",
            "   private MyActivityPermissionsDispatcher() {",
            "   }",
            "   static void showCameraWithCheck(MyActivity target) {",
            "        if (PermissionUtils.hasSelfPermissions(target, PERMISSION_SHOWCAMERA)) {",
            "                target.showCamera();",
            "        } else if (PermissionUtils.shouldShowRequestPermissionRationale(target, PERMISSION_SHOWCAMERA)) {",
            "                target.onCameraRationale();",
            "        } else {",
            "                ActivityCompat.requestPermissions(target, PERMISSION_SHOWCAMERA, REQUEST_SHOWCAMERA);",
            "        }",
            "   }",
            "   static void onRequestPermissionsResult(MyActivity target, int requestCode, int[] grantResults) {",
            "        switch (requestCode) {",
            "                case REQUEST_SHOWCAMERA:",
            "                if (PermissionUtils.verifyPermissions(grantResults)) {",
            "                        target.showCamera();",
            "                }",
            "                break;",
            "                default:",
            "                        break;",
            "        }",
            "   }",
            "}"
    )
}

