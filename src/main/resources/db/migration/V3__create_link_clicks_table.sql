CREATE TABLE link_clicks
(
    id            BIGSERIAL PRIMARY KEY,
    short_link_id BIGINT                   NOT NULL,
    clicked_at    TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT fk_link_clicks_short_link
        FOREIGN KEY (short_link_id)
            REFERENCES short_links (id)
)