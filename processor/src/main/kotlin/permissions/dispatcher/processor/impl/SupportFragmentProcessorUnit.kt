package permissions.dispatcher.processor.impl

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.MethodSpec
import permissions.dispatcher.processor.ProcessorUnit
import permissions.dispatcher.processor.RuntimePermissionsElement
import permissions.dispatcher.processor.util.permissionFieldName
import permissions.dispatcher.processor.util.requestCodeFieldName
import permissions.dispatcher.processor.util.simpleString
import permissions.dispatcher.processor.util.typeMirrorOf
import javax.lang.model.element.ExecutableElement
import javax.lang.model.type.TypeMirror

/**
 * ProcessorUnit implementation for Fragments defined in the support-v4 library
 */
class SupportFragmentProcessorUnit: BaseProcessorUnit() {

    private val PERMISSION_UTILS: ClassName = ClassName.get("permissions.dispatcher", "PermissionUtils")
    private val ACTIVITY: ClassName = ClassName.get("android.app", "Activity")

    override fun checkSupported(): Boolean {
        try {
            Class.forName("android.support.v4.app.Fragment")
            println("Support Fragment supported")
            return true
        } catch (e: Exception) {
            println("Support Fragment not supported: ${e}")
            return false
        }
    }

    override fun getTargetType(): TypeMirror {
        return typeMirrorOf("android.support.v4.app.Fragment")
    }

    override fun addWithCheckBody(builder: MethodSpec.Builder, needsMethod: ExecutableElement, rpe: RuntimePermissionsElement, targetParam: String) {
        // Create field names for the constants to use
        val requestCodeField = requestCodeFieldName(needsMethod)
        val permissionField = permissionFieldName(needsMethod)

        // Obtain the Activity reference for the fragment
        val activityVar = "activity"
        builder.addStatement("\$T \$N = \$N.getActivity()", ACTIVITY, activityVar, targetParam)

        // Add the conditional for when permission has already been granted
        builder.beginControlFlow("if (\$T.hasSelfPermissions(\$N, \$N))", PERMISSION_UTILS, activityVar, permissionField)
        builder.addStatement("\$N.\$N()", targetParam, needsMethod.simpleString())

        // Add the conditional for "OnRationale", if present
        val onRationale: ExecutableElement? = rpe.findOnRationaleForNeeds(needsMethod)
        if (onRationale != null) {
            builder.nextControlFlow("else if (\$T.shouldShowRequestPermissionRationale(\$N, \$N))", PERMISSION_UTILS, activityVar, permissionField)
            builder.addStatement("\$N.\$N()", targetParam, onRationale.simpleString())
        }

        // Add the branch for "request permission"
        builder.nextControlFlow("else")
        builder.addStatement("\$N.requestPermissions(\$N, \$N)", targetParam, permissionField, requestCodeField)
        builder.endControlFlow()
    }

    override fun addResultCaseBody(builder: MethodSpec.Builder, needsMethod: ExecutableElement, rpe: RuntimePermissionsElement, targetParam: String, grantResultsParam: String) {
        // Add the conditional for "permission verified"
        builder.beginControlFlow("if (\$T.verifyPermissions(\$N))", PERMISSION_UTILS, grantResultsParam)
        builder.addStatement("target.\$N()", needsMethod.simpleString())

        // Add the conditional for "permission denied", if present
        val onDenied: ExecutableElement? = rpe.findOnDeniedForNeeds(needsMethod)
        if (onDenied != null) {
            builder.nextControlFlow("else")
            builder.addStatement("\$N.\$N()", targetParam, onDenied.simpleString())
        }
        // Close the control flow
        builder.endControlFlow()
        builder.addStatement("break");
    }
}