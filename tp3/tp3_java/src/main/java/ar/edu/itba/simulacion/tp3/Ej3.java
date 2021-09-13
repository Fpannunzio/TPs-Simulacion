package ar.edu.itba.simulacion.tp3;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

import ar.edu.itba.simulacion.particle.Particle2D;
import ar.edu.itba.simulacion.particle.ParticleGeneration;
import ar.edu.itba.simulacion.tp3.BrownianParticleSystem.SimulationState;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

public class Ej3 {
    
    public static void main( String[] args ) throws IOException {
        
        if(args.length < 1) {
            throw new IllegalArgumentException("First argument must be config path");
        }
        final ObjectMapper mapper = new ObjectMapper();

        final Ej3Config config = mapper.readValue(new File(args[0]), Ej3Config.class);
        
        final List<RoundSummary> summary = new ArrayList<>(config.getTemperatures().length);
        
        for(final int temp : config.getTemperatures()) {
            
            System.out.printf("Working with temperature %d.\n", temp);

            config.particleGen.get(1).minVelocity = temp;
            config.particleGen.get(1).maxVelocity = temp + 1;

            final List<Particle2D> initialState =  ParticleGeneration.particleGenerator(config.getParticleGen());
            final BrownianParticleSystem brownianSystem = new BrownianParticleSystem(config.spaceWidth, initialState);
            
            brownianSystem.calculateNCollision(config.getIterations());

            final List<Double[]> bigParticleStates = brownianSystem.getStates()
                .stream()
                .map(SimulationState::getParticles)
                .map(particles -> new Double[]{particles.get(0).getX(), particles.get(0).getY()})
                .collect(Collectors.toList())
                ;

            summary.add(new RoundSummary(bigParticleStates, temp));
        }

        mapper.writeValue(new File(config.outputFile), summary);
    }

    @Data
    @Jacksonized
    @Builder(setterPrefix = "with")
    public static class Ej3Config {
        public List<ParticleGeneration.ParticleGenerationConfig>    particleGen;
        public int[]                                                temperatures;
        public int                                                  iterations;
        public double                                               spaceWidth;
        public String                                               outputFile;
    }

    @Data
    @Jacksonized
    @Builder(setterPrefix = "with")
    public static class RoundSummary {
        public List<Double[]>               states;
        public int                          temp;
    }

}
