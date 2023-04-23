using Distributions, Memoize, Folds, Transducers, Plots, Printf, Formatting, YAML

@memoize Dict function create_log_normal_distribution(min, max)
    """
    Build a log 1M element normal distribution around the minimum and maximum values
    """

    mean = (log(min) + log(max)) / 2
    std_dev = (log(max) - log(min)) / 3.29
    rand(LogNormal(mean, std_dev), 1_000_000)
end

function compute_inherent_loss(probability::Float64, min, max)::Float64
    exp(((log(max) + log(min)) / 2) + ((log(max) - log(min)) / 3.28971)^2 / 2) * probability
end

function compute_random_loss(probability::Float64, min, max)::Float64
    if rand() < probability
        rand(create_log_normal_distribution(min, max))
    else
        0
    end
end

function run_single_simulation(probability::Float64, min, max)
    return compute_inherent_loss(probability, min, max), compute_random_loss(probability, min, max)
end

function area_under(info::Vector{Float64})::Tuple{Vector{Float64},Vector{Float64}}

    values = Float64[]
    percentages = Float64[]
    sorted = sort(info, rev=true)
    the_len = length(info)
    if the_len > 0

        latest = @inbounds sorted[1]
        latest_cnt = 1
        for i in 1:the_len
            this = @inbounds sorted[i]
            if this != latest
                push!(values, latest)
                push!(percentages, i / the_len)
                latest_cnt = i
                latest = this
            end
        end
    end

    reverse(values), reverse(percentages)
end

function run_multiple_simultations(probability::Float64, min, max, iterations=100_000)
    ret = Vector{Float64}(undef, iterations)
    for i in range(1, iterations)
        @inbounds ret[i] = compute_random_loss(probability, min, max)
    end
    ret
end

dog = run_multiple_simultations(0.12, 2_000_000, 16_000_000)

catfood = area_under(dog)

moosefood = area_under(run_multiple_simultations(0.08, 6_000_000, 40_000_000))

plot([catfood[1], moosefood[1]], [catfood[2], moosefood[2]], lw=4, xformatter=(n -> "\$" * format(n, commas=true, precision=0)),
    yformatter=(x -> if x == 0.0
        ""
    else
        format(x * 100.0, precision=0) * "%"
    end), xaxis=:log10)

my_str = """
-
  type: estimate
  name: Ransomware
  probability: 0.12
  lower: 2000000
  upper: 16000000
-
    type: estimate
    name: Email Compromise
    probability: 0.10
    lower: 50000
    upper: 2000000
-
    type: estimate
    name: Cloud Compromise/Breach
    probability: 0.05
    lower: 500000
    upper: 10000000
-
    type: estimate
    name: Cloud Compromise/Disruption
    probability: 0.10
    lower: 2000000
    upper: 5000000
-
    type: estimate
    name: SaaS Compromise/Breach
    probability: 0.05
    lower: 500000
    upper: 10000000
-
    type: estimate
    name: SaaS Compromise/Disruption
    probability: 0.1
    lower: 2000000
    upper: 5000000
-
    type: estimate
    name: Insider/Breach
    probability: 0.05
    lower: 500000
    upper: 10000000
-
    type: estimate
    name: Insider/Disruption
    probability: 0.1
    lower: 2000000
    upper: 5000000
-
    type: estimate
    name: Insider/Fraud
    probability: 0.05
    lower: 50000
    upper: 2000000
-
  type: losses
  name: Sally da Boss
  acceptable:
    -
        probability: 0.99
        loss: 100000
    -
        probability: 0.15
        loss: 2000000
    -
        probability: 0.02
        loss: 10000000

    -
        probability: 0.005
        loss: 50000000
"""

to_analyze = YAML.load(my_str)

struct Estimate
    name::String
    probability::Float64
    lower::Float64
    upper::Float64
end

struct Acceptable
    probability::Float64
    loss::Float64
end

struct Loss
    name::String
    acceptable::Vector{Acceptable}
end

function raw_to_monte(to_analyze::Vector{Dict{Any,Any}})

    estimates = map(filter(v -> v["type"] == "estimate", to_analyze)) do v
        Estimate(convert(String, v["name"]),
            convert(Float64, v["probability"]),
            convert(Float64, v["lower"]),
            convert(Float64, v["upper"]))
    end


    losses = map(filter(to_analyze) do v
        v["type"] == "losses"
    end
    ) do v
        Loss(convert(String, v["name"]),  map(v["acceptable"]) do a
            Acceptable(convert(Float64, a["probability"]), convert(Float64, a["loss"]))
        end)
    end

    simulations = map(estimates) do e
        Dict(:name => e.name, :data => area_under(run_multiple_simultations(e.probability, e.lower, e.upper)))
    end

    x_axis = [map(simulations) do s
        s[:data][1]
    end ; map(v -> map(v2 -> v2.loss, v.acceptable), losses)]

    y_axis = [map(simulations) do s
        s[:data][2]
    end; map(v -> map(v2 -> v2.probability, v.acceptable), losses)]

    labels = [map(x -> x[:name], simulations); map(v -> "Loss Exceedance Tolerance: " * v.name, losses)]


    the_plot = plot(x_axis,
        y_axis,
        label=reshape(labels, 1, length(labels)),
        lw=4, xformatter=(n -> "\$" * format(n, commas=true, precision=0)),
        yformatter=(x -> if x == 0.0
            ""
        else
            format(x * 100.0, precision=0) * "%"
        end), xaxis=:log10,
        xlims=(1_000_000.0, 100_000_000.0),
        ylims=(0.0, 0.3),
        )
        

    the_plot

end

raw_to_monte(to_analyze)

png("risky_business.png")