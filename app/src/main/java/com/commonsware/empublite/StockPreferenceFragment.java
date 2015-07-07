package com.commonsware.empublite;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;

@TargetApi(11)
public class StockPreferenceFragment extends PreferenceFragment {

    /*

    Being a stock preference fragment, this fragment can used for multiple
    preference headers.

    The mission of PreferenceFragment is to call addPreferencesFromResource() in onCreate(),
    supplying the resource ID of the preference XML to load for a particular preference header.

     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
            getActivity() - Return the Activity this fragment is currently associated with.
            getResources() - Return a Resources instance for your application's package.
            getIdentifier() ->
                Return a resource identifier for the given resource name.
                A fully qualified resource name is of the form "package:type/entry".
                The first two components (package and type) are optional if defType and defPackage,
                respectively, are specified here.

                Note: use of this function is discouraged. It is much more efficient to retrieve
                resources by identifier than by name.

                Parameters:
                    name:   The name of the desired resource.
                    defType:	Optional default resource type to find, if "type/" is not
                        included in the name. Can be null to require an explicit type.
                    defPackage:	Optional default package to find, if "package:" is not included
                        in the name. Can be null to require an explicit package.

                Returns:
                    int The associated resource identifier. Returns 0 if no such resource
                    was found. (0 is not a valid resource ID.)


            getArguments() - Return the arguments supplied to setArguments(Bundle), if any.
            getString() ->

                Return a localized string from the application's package's default string table.

                Parameters:
                    resId:	Resource id for the string


            FROM BOOK p441:
                The <extra> elements in our preference header XML supply the name of the
                preference XML to be loaded. We get that name via the arguments Bundle
                (getArguments().getString("resource")).

                Note that getIdentifier() uses reflection to find this value, and so there is some
                overhead in the process. Do not use getIdentifier() in a long loop – cache the
                value instead.


         */

        int res=getActivity()
                .getResources()
                .getIdentifier(getArguments().getString("resource"),
                        "xml",
                        getActivity().getPackageName());

        addPreferencesFromResource(res);
    }
}