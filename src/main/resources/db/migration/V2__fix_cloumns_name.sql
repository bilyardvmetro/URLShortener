ALTER TABLE short_links
    RENAME COLUMN shortcode TO short_code;

ALTER TABLE short_links
    RENAME COLUMN createdat TO created_at;