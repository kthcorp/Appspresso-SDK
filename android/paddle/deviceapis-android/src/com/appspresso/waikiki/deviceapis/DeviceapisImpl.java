package com.appspresso.waikiki.deviceapis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.appspresso.w3.Feature;

public class DeviceapisImpl {

    /**
     * 이용가능한 feature의 목록을 반환
     * 
     * @param configuredtFeatures 이용가능한 feature목록
     * @return
     */
    public Feature[] listAvailableFeatures(Feature[] configuredtFeatures) {
        Map<String, DeviceapisFeature> features =
                createFeatures(FeatureConstants.WAIKIKI_FEATURE_LIST);
        fillInfoInFeatures(features, configuredtFeatures);

        Feature[] availableFeatures = new Feature[features.size()];
        int i = 0;
        for (Feature f : features.values()) {
            availableFeatures[i++] = f;
        }

        return availableFeatures;
    }

    /**
     * 활성활된 feature의 목록을 반환
     * 
     * @param configuredtFeatures 활성화된 feature 목록
     * @return
     */
    public Feature[] listActivatedFeatures(Feature[] configuredtFeatures) {
        Map<String, DeviceapisFeature> features =
                createFeatures(FeatureConstants.WAIKIKI_FEATURE_LIST);
        fillInfoInFeatures(features, configuredtFeatures);

        List<Feature> activatedFeatures = new ArrayList<Feature>();
        for (DeviceapisFeature f : features.values()) {
            if (f.isConfigured()) {
                activatedFeatures.add(f);
            }
        }

        return activatedFeatures.toArray(new Feature[] {});
    }

    /**
     * 매개변수로 전달되는 URI에 해당되는 feature 맵을 반환
     * 
     * @param uris URI 목록
     * @return feature 맵
     */
    public static Map<String, DeviceapisFeature> createFeatures(String[] uris) {
        Map<String, DeviceapisFeature> waikikiFeatures = new HashMap<String, DeviceapisFeature>();
        for (String uri : uris) {
            waikikiFeatures.put(uri, new DeviceapisFeature(uri));
        }

        return waikikiFeatures;
    }

    /**
     * 해당 feature 목록에 설정된 feature들의 속성을 채움.
     * 
     * @param targetFeatures 속성을 채울 feature 목록
     * @param configuredtFeatures 설정된 feature 목록
     */
    public static void fillInfoInFeatures(Map<String, DeviceapisFeature> targetFeatures,
            Feature[] configuredtFeatures) {
        String uri;
        DeviceapisFeature feature;

        for (Feature f : configuredtFeatures) {
            uri = f.getUri();

            if (targetFeatures.containsKey(uri)) {
                feature = targetFeatures.get(uri);
                feature.setConfigured(true);
                feature.setRequired(f.isRequired());
                feature.setParams(f.getParams());
            }
        }
    }
}
