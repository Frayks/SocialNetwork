package andrew.project.socialNetwork.backend;

import andrew.project.socialNetwork.backend.api.constants.RoleName;
import andrew.project.socialNetwork.backend.api.entities.Role;
import andrew.project.socialNetwork.backend.api.properties.SystemProperties;
import andrew.project.socialNetwork.backend.api.services.RoleService;
import andrew.project.socialNetwork.backend.libraries.MainLibImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Initializer implements InitializingBean {

    private static final Logger LOGGER = LogManager.getLogger(Initializer.class);

    private SystemProperties systemProperties;
    private RoleService roleService;

    public Initializer() {
    }

    @Override
    public void afterPropertiesSet() {
        LOGGER.info(String.format("initMainData=%b", systemProperties.isInitMainData()));
        if (systemProperties.isInitMainData()) {
            Role role = new Role();
            role.setRoleName(RoleName.USER);
            roleService.save(role);
        }
    }

    @Autowired
    public void setSystemProperties(SystemProperties systemProperties) {
        this.systemProperties = systemProperties;
    }

    @Autowired
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

}
