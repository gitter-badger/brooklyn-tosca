package org.apache.brooklyn.tosca.a4c.brooklyn;

import org.apache.brooklyn.api.entity.Application;
import org.apache.brooklyn.api.entity.EntitySpec;
import org.apache.brooklyn.entity.software.base.VanillaSoftwareProcess;
import org.apache.brooklyn.tosca.a4c.Alien4CloudToscaTest;
import org.apache.brooklyn.util.core.ResourceUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class ToscaPlanToSpecTransformerTest extends Alien4CloudToscaTest {

    protected ToscaPlanToSpecTransformer transformer;

    @BeforeMethod
    public void setup() throws Exception {
        super.setup();
        transformer = new ToscaPlanToSpecTransformer();
        transformer.injectManagementContext(getMgmt());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSimpleHostedTopologyParser() {
        String templateUrl = getClasspathUrlForResource("templates/script1.tosca.yaml");

        EntitySpec<? extends Application> app = transformer.createApplicationSpec(
                new ResourceUtils(mgmt).getResourceAsString(templateUrl));

        assertNotNull(app);
        assertEquals(app.getChildren().size(), 1);

        EntitySpec<VanillaSoftwareProcess> hostVanilla =
                (EntitySpec<VanillaSoftwareProcess>)app.getChildren().get(0);
        assertEquals(hostVanilla.getChildren().size(), 1);

        EntitySpec<VanillaSoftwareProcess> hostedSoftwareComponent =
                (EntitySpec<VanillaSoftwareProcess>)hostVanilla.getChildren().get(0);

        assertEquals(hostVanilla.getFlags().get("tosca.node.type"), "tosca.nodes.Compute");
        assertEquals(hostVanilla.getType().getName(),
                "org.apache.brooklyn.entity.software.base.VanillaSoftwareProcess");
        assertEquals(hostVanilla.getLocations().size(), 1);
        assertEquals(hostVanilla.getLocations().get(0).getDisplayName(), "localhost");

        assertEquals(hostedSoftwareComponent.getFlags().get("tosca.node.type"),
                "tosca.nodes.SoftwareComponent");
        assertEquals(hostedSoftwareComponent.getType().getName(),
                "org.apache.brooklyn.entity.software.base.VanillaSoftwareProcess");
    }


}
