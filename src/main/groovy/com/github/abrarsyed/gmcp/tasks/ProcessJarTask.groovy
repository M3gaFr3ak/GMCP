package com.github.abrarsyed.gmcp.tasks

import com.github.abrarsyed.gmcp.Util
import net.md_5.specialsource.*
import net.md_5.specialsource.provider.JarProvider
import net.md_5.specialsource.provider.JointProvider
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

public class ProcessJarTask extends CachedTask
{
    @InputFile
    def inJar;

    @InputFile
    def exceptorJar;

    @InputFile
    def srg;

    @InputFile
    def exceptorCfg;

    @OutputFile
    @CachedTask.Cached
    def outJar;

    @OutputFile
    @CachedTask.Cached
    def outMap;

    @InputFiles
    private ArrayList<Object> ats = new ArrayList<Object>();

    /**
     * adds an access transformer to the deobfuscation of this
     *
     * @param obj
     */
    public void addTransformer(Object... obj)
    {
        for (Object object : obj)
        {
            ats.add(object);
        }
    }

    @TaskAction
    public void doTask() throws IOException
    {
        // import from the extension
        addTransformer project.minecraft.accessTransformers as Object[]

        // resolve files
        File tempObfJar = new File(getTemporaryDir(), "deobfed.jar"); // courtesy of gradle temp dir.
        inJar = project.file(inJar)
        exceptorJar = project.file(exceptorJar)
        srg = project.file(srg)
        exceptorCfg = project.file(exceptorCfg)
        outJar = project.file(outJar)
        outMap = project.file(outMap)

        // make the ATs LIST
        ArrayList<File> ats = this.ats.collect { project.file(it) }

        // deobf
        logger.lifecycle("Applying SpecialSource...");
        deobfJar(inJar, tempObfJar, srg, ats);

        // apply exceptor
        logger.lifecycle("Applying Exceptor...");
        applyExceptor(exceptorJar, tempObfJar, outJar, exceptorCfg, new File(getTemporaryDir(), "exceptorLog"));
    }

    def void deobfJar(File inJar, File outputJar, File srg, ArrayList<File> ats) throws IOException
    {
        logger.debug("INPUT: " + inJar);
        logger.debug("OUTPUT: " + outputJar);
        // load mapping
        JarMapping mapping = new JarMapping();
        mapping.loadMappings(srg);

        // load in ATs
        AccessMap accessMap = new AccessMap();
        logger.info("Using AccessTransformers...");
        ats.each {
            logger.info("" + it);
            accessMap.loadAccessTransformer(it);
        }

        // make a processor out of the ATS and mappings.
        RemapperPreprocessor processor = new RemapperPreprocessor(null, mapping, accessMap);

        // make remapper
        JarRemapper remapper = new JarRemapper(processor, mapping);

        // load jar
        Jar input = Jar.init(inJar);

        // ensure that inheritance provider is used
        JointProvider inheritanceProviders = new JointProvider();
        inheritanceProviders.add(new JarProvider(input));
        mapping.setFallbackInheritanceProvider(inheritanceProviders);

        // remap jar
        remapper.remapJar(input, outputJar);

        // output inheritance map
        mapping.inheritanceMap.save(new PrintWriter(outMap.newWriter()))

    }

    public void applyExceptor(final File injectorJar, final File inJar, final File outputJar, final File config, final File log)
    {
        logger.debug "INPUT: " + inJar
        logger.debug "OUTPUT: " + outputJar
        logger.debug "CONFIG: " + config

        // http://www.gradle.org/docs/current/dsl/org.gradle.api.tasks.JavaExec.html
        project.javaexec {
            args(
                    injectorJar.getAbsolutePath(),
                    inJar.getAbsolutePath(),
                    outputJar.getAbsolutePath(),
                    config.getAbsolutePath(),
                    log.getAbsolutePath()
            );

            //jvmArgs("-jar", injectorJar.getAbsolutePath());

            setMain "-jar"
            //settable(injectorJar);
            setWorkingDir injectorJar.getParentFile();

            classpath Util.getClassPath();

            setStandardOutput Util.getNullStream();
        }
    }
    
    public File getInJar()
    {
        if (inJar instanceof File)
            return (File)inJar;
        else
        {
            inJar = getProject().file(inJar);
            return (File)inJar;
        }
    }

    public File getOutJar()
    {
        if (outJar instanceof File)
            return (File)outJar;
        else
        {
            outJar = getProject().file(outJar);
            return (File)outJar;
        }
    }

    public File getOutMap()
    {
        if (outMap instanceof File)
            return (File)outMap;
        else
        {
            outMap = getProject().file(outMap);
            return (File)outMap;
        }
    }


    public File getExceptorJar()
    {
        if (exceptorJar instanceof File)
            return (File)exceptorJar;
        else
        {
            exceptorJar = getProject().file(exceptorJar);
            return (File)exceptorJar;
        }
    }

    public File getSrg()
    {
        if (srg instanceof File)
            return (File)srg;
        else
        {
            srg = getProject().file(srg);
            return (File)srg;
        }
    }

    public File getExceptorCfg()
    {
        if (exceptorCfg instanceof File)
            return (File)exceptorCfg;
        else
        {
            exceptorCfg = getProject().file(exceptorCfg);
            return (File)exceptorCfg;
        }
    }
}
