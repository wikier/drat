/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package drat.proteus.rest;

import backend.FileConstants;
import com.google.gson.Gson;
import drat.proteus.services.general.Item;
import drat.proteus.services.general.ServiceStatus;
import drat.proteus.services.health.HealthMonitorService;
import drat.proteus.services.licenses.RatInstanceService;
import drat.proteus.services.mimetype.MimeTypeBreakdownService;
import drat.proteus.services.product.RecentProductService;
import drat.proteus.services.repo.Repository;
import org.wicketstuff.rest.annotations.MethodMapping;
import org.wicketstuff.rest.annotations.parameters.RequestParam;
import org.wicketstuff.rest.resource.gson.GsonRestResource;
import org.wicketstuff.rest.utils.http.HttpMethod;
import java.util.List;
import java.util.Map;

public class ServicesRestResource extends GsonRestResource {
    private RecentProductService productService;
    private HealthMonitorService healthMonitorService;
    private MimeTypeBreakdownService mimeTypeBreakdownService;
    private RatInstanceService ratInstanceService;
    public ServicesRestResource() {
        productService = new RecentProductService();
        healthMonitorService = new HealthMonitorService();
        mimeTypeBreakdownService = new MimeTypeBreakdownService();
        ratInstanceService = new RatInstanceService();
    }

    @MethodMapping(value = "/repo/licenses/unapproved", httpMethod = HttpMethod.GET)
    public List<Item> getUnapprovedLicensesFromRatInstances() {
        ratInstanceService.getRatLogs();
        return ratInstanceService.getUnapprovedLicenses();
    }

    @MethodMapping(value = "/products", httpMethod = HttpMethod.GET)
    public List<Item> getRecentProducts() {
        return productService.getAllRecentProducts();
    }

    @MethodMapping(value = "/repo/breakdown/mime", httpMethod = HttpMethod.GET)
    public List<Item> getRepoMimeTypeBreakdown(@RequestParam(value = "limit", required = false) Integer limit) {
        if(limit == null) {
            limit = 0;
        }
        return mimeTypeBreakdownService.getMimeTypes(limit);
    }

    @MethodMapping(value = "/repo/breakdown/license", httpMethod = HttpMethod.GET)
    public List<Item> getRepoLicenseTypeBreakdown() {
        ratInstanceService.getRatLogs();
        return ratInstanceService.getLicenseTypeBreakdown();
    }

    @MethodMapping(value = "/repo/size", httpMethod = HttpMethod.GET)
    public Repository.Size getRepositorySize() {
        return new Repository(FileConstants.DRAT_TEMP_UNZIPPED_PATH).getSize();
    }

    @MethodMapping(value = "/status/drat", httpMethod = HttpMethod.GET)
    public ServiceStatus getDratRunningStatus() {
        return healthMonitorService.getDratStatus();
    }

    @MethodMapping(value = "/status/oodt", httpMethod = HttpMethod.GET)
    public ServiceStatus getOodtRunningStatus() {
        return healthMonitorService.getOodtStatus();
    }

    @MethodMapping(value = "/status/oodt/raw", httpMethod = HttpMethod.GET)
    public Object getOodtRawHealthStatus() {
        String jsonBody = healthMonitorService.rerouteHealthMonitorData().readEntity(String.class);
        return (Map<String,Object>)new Gson().fromJson(jsonBody, Map.class);
    }
}
