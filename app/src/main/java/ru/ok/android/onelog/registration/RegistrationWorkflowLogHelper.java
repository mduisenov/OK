package ru.ok.android.onelog.registration;

import java.util.HashMap;
import ru.ok.android.fragments.AvatarUploadFragment;
import ru.ok.android.onelog.OneLog;
import ru.ok.android.services.processors.registration.AuthorizationPreferences;
import ru.ok.android.ui.nativeRegistration.ChooseRegistrationFragment;
import ru.ok.android.ui.nativeRegistration.EnterNewLoginFragment;
import ru.ok.android.ui.nativeRegistration.ExistingUserFragment;
import ru.ok.android.ui.nativeRegistration.FirstEnterActivity;
import ru.ok.android.ui.nativeRegistration.LoginFragment;
import ru.ok.android.ui.nativeRegistration.UpdateUserInfoFragment;
import ru.ok.android.ui.nativeRegistration.UserListFragment;
import ru.ok.onelog.builtin.Outcome;
import ru.ok.onelog.registration.RegistrationWorkflowEventFactory;
import ru.ok.onelog.registration.RegistrationWorkflowSource;

public class RegistrationWorkflowLogHelper {
    private static final HashMap<Class, RegistrationWorkflowSource> REGISTRATION_SOURCES;

    static {
        REGISTRATION_SOURCES = new HashMap();
        REGISTRATION_SOURCES.put(ExistingUserFragment.class, RegistrationWorkflowSource.existing_user);
        REGISTRATION_SOURCES.put(UserListFragment.class, RegistrationWorkflowSource.choose_user);
        REGISTRATION_SOURCES.put(EnterNewLoginFragment.class, RegistrationWorkflowSource.create_user);
        REGISTRATION_SOURCES.put(UpdateUserInfoFragment.class, RegistrationWorkflowSource.fill_profile);
        REGISTRATION_SOURCES.put(AvatarUploadFragment.class, RegistrationWorkflowSource.upload_avatar);
        REGISTRATION_SOURCES.put(LoginFragment.class, RegistrationWorkflowSource.enter_reg);
        REGISTRATION_SOURCES.put(FirstEnterActivity.class, RegistrationWorkflowSource.enter_reg);
        REGISTRATION_SOURCES.put(ChooseRegistrationFragment.class, RegistrationWorkflowSource.choose_reg);
    }

    public static RegistrationWorkflowSource getWorkflowSource(Class callerClass) {
        return (RegistrationWorkflowSource) REGISTRATION_SOURCES.get(callerClass);
    }

    public static void log(Class callerClass, Outcome outcome) {
        RegistrationWorkflowSource source = getWorkflowSource(callerClass);
        if (source != null) {
            log(source, outcome);
        }
    }

    public static void log(RegistrationWorkflowSource source, Outcome outcome) {
        if (source != null) {
            if (source == RegistrationWorkflowSource.enter_reg && AuthorizationPreferences.getPermissionsRequestOnSeparateScreen()) {
                source = RegistrationWorkflowSource.enter_choose_reg;
            }
            OneLog.log(RegistrationWorkflowEventFactory.get(source, outcome));
        }
    }
}
